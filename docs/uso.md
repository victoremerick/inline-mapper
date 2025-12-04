# Como usar o Inline Mapper

Este guia mostra o fluxo essencial: adicionar a dependencia, anotar entidades e mapear linhas ou arquivos inteiros. Os exemplos usam Java 11+.

## Fluxo essencial em 3 passos
1) Anote a classe com `@LineEntity` e campos com `@Column` definindo posicao (0-based) e tamanho fixo. 
2) Crie o mapper com `PositionalLineMapper` (opcionalmente passando um `TypeConverterRegistry`).
3) Converta de/para linhas com `toObject` ou `toLine`.

```java
import annotation.com.emerick.inlinemapper.LineEntity;
import annotation.com.emerick.inlinemapper.Column;
import mapper.com.emerick.inlinemapper.LineMapper;
import mapper.com.emerick.inlinemapper.PositionalLineMapper;

@LineEntity
public class Person {
    @Column(position = 0, length = 10)
    public String name;

    @Column(position = 10, length = 3)
    public Integer age;

    @Column(position = 13, length = 20)
    public String email;
}

LineMapper<Person> mapper = new PositionalLineMapper<>(Person.class);
Person p = mapper.toObject("John      025john@example.com   ");
String line = mapper.toLine(p);
```

## Trabalhando com colecoes de linhas

```java
List<String> lines = List.of(
    "John      025john@example.com   ",
    "Jane      030jane@example.com   "
);
List<Person> people = mapper.toObjects(lines);
List<String> out = mapper.toLines(people);
```

## Mapeando arquivos completos
Use `@FileLayout` e `@FileSegment` ou o builder para descrever posicoes absolutas, curingas e indices negativos (a partir do fim do arquivo).

```java
import annotation.com.emerick.inlinemapper.FileLayout;
import annotation.com.emerick.inlinemapper.FileSegment;
import mapper.com.emerick.inlinemapper.FileLayoutBuilder;
import mapper.com.emerick.inlinemapper.FileMapper;
import mapper.com.emerick.inlinemapper.FileMappingResult;

@FileLayout
class MyFileLayout {
    @FileSegment(position = 1) HeaderLine header;
    @FileSegment(wildcard = true) List<DetailLine> details;
    @FileSegment(position = -1) TrailerLine trailer;
}

FileMapper fileMapper = new FileMapper(FileLayoutBuilder.fromAnnotations(MyFileLayout.class));
FileMappingResult result = fileMapper.map(lines);
HeaderLine header = result.getSingle("header", HeaderLine.class);
List<DetailLine> detailLines = result.getList("details", DetailLine.class);
```

## Converters personalizados
Implemente `AbstractTypeConverter<T>` para tipos de dominio (ex.: datas em formatos proprios, moedas, identificadores).

```java
import converter.com.emerick.inlinemapper.AbstractTypeConverter;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class YearMonthConverter extends AbstractTypeConverter<YearMonth> {
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public YearMonth fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        return trimmed != null ? YearMonth.parse(trimmed, F) : null;
    }

    @Override
    public Class<YearMonth> getType() { return YearMonth.class; }
}

TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(new YearMonthConverter());
LineMapper<MyEntity> mapper = new PositionalLineMapper<>(MyEntity.class, registry);
```

## Boas praticas rapidas
- Defina comprimentos coerentes com o arquivo; sobrescreva com zeros ou espacos quando necessario.
- Use `defaultValue` em `@Column` para preencher colunas vazias sem logica extra.
- Ative `trim` (default) para evitar falhas por espacos em branco; desative quando o espaco fizer parte do valor.
- Separe conversores em classes reutilizaveis e registre-os em um unico `TypeConverterRegistry`.
- Em arquivos longos, prefira o builder de layout para leitura mais declarativa.
