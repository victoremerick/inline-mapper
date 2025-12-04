# Perguntas e respostas (FAQ)

**Preciso de qual versao do Java?**  Java 11 ou superior, conforme propriedades do `pom.xml`.

**Como tratar espacos extras nos campos?**  `@Column` usa `trim = true` por padrao. Defina `trim = false` se o espaco fizer parte do valor.

**O que acontece quando o valor nao cabe no comprimento definido?**  `PositionalLineMapper` gera erro de mapeamento. Ajuste `length` ou trate o valor antes de converter.

**Posso mapear listas ou colecoes?**  Sim, use `ListConverter` para listas simples e `@FileSegment(wildcard = true)` em layouts de arquivo para seções de tamanho variavel.

**Como adicionar um novo tipo?**  Implemente `AbstractTypeConverter<T>` (veja `TYPE_SYSTEM.md`) e registre com `TypeConverterRegistry.register(...)` antes de criar o mapper.

**Consigo ler e gravar usando o mesmo modelo?**  Sim, a API e bidirecional: `toObject`/`toObjects` leem e `toLine`/`toLines` escrevem usando a mesma definicao de entidade.

**Como lidar com datas em formatos diferentes?**  Registre um `LocalDateConverter` com o padrao desejado (ex.: `new LocalDateConverter("ddMMyyyy")`) ou crie um conversor customizado.

**Indices negativos funcionam como?**  Em `@FileSegment`, valores negativos contam a partir do fim do arquivo (-1 e a ultima linha, -2 e a penultima). Excelente para rodapes e trailers.

**Como depurar erros de conversao?**  A excecao traz o campo, posicao e valor original. Compare com o layout esperado e revise o `TypeConverter` associado.

**Onde encontrar exemplos completos?**  Veja `src/test/java/com/example/inlinemapper/example/` e os guias `TYPE_SYSTEM.md` e `DOCUMENTATION_INDEX.md` para caminhos rapidos.
