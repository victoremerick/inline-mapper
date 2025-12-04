# Instalar via pom

Adicione a dependencia ao seu projeto Maven e confirme o build com Java 11+. Estas instrucoes usam as coordenadas definidas no `pom.xml` do projeto.

## Dependencia principal
```xml
<dependency>
    <groupId>com.emerick</groupId>
    <artifactId>inline-mapper</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Requisitos de build
- Java 11 ou superior
- Maven 3.6+ (o projeto usa plugins de compilacao, fonte e javadoc atualizados)

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>
```

## Build e testes locais
1. Rode `mvn clean install` para compilar e instalar no seu `.m2` local.
2. Para testar: `mvn test`.
3. Para gerar artefatos assinados (quando publicar): `mvn clean deploy` com `settings.xml` configurado.

## Publicacao e credenciais
- O `settings.xml.template` mostra como informar `SONATYPE_USERNAME`, `SONATYPE_PASSWORD` e `GPG_PASSPHRASE`.
- Para releases, configure tambem `GPG_PRIVATE_KEY` nas variaveis da pipeline/CI.
- Utilize o plugin `nexus-staging-maven-plugin` (ja configurado no `pom.xml`) para fechar e liberar o staging no Sonatype.

## Verificacao rapida
- Dependencia resolvida? Rode `mvn dependency:tree` e confira se `com.emerick:inline-mapper:0.0.1` aparece.
- Compatibilidade de tipos? Revise `TYPE_SYSTEM.md` e `CONVERTER_REFERENCE.md` para saber quais conversores usar.
- Versao errada? Atualize o valor de `<version>` e recompile.
