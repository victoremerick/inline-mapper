# Inline Mapper Docs

Inline Mapper e uma biblioteca Java leve para converter linhas posicionais de largura fixa em objetos e gerar linhas a partir desses objetos. Este conjunto de paginas serve como estrutura do site da biblioteca, focando em uso rapido, instalacao via Maven e respostas para duvidas comuns.

## Navegacao rapida
- [Como usar](./uso.md): passo a passo para mapear linhas e arquivos, com exemplos prontos.
- [Instalar via pom](./pom.md): dependencias, configuracao de build e verificacao rapida.
- [Perguntas e respostas](./faq.md): solucoes para problemas frequentes.

## Quando usar
- Precisa ler ou gerar arquivos posicionais (fixed-width) de forma tipada.
- Quer anotar classes com posicoes e comprimentos em vez de manipular substrings manualmente.
- Deseja adicionar conversores de tipos customizados sem reescrever o parsing.

## Principais recursos
- Anotacoes `@LineEntity` e `@Column` para mapear campos a posicoes fixas.
- Conversores prontos (String, Integer, Long, Double, Boolean, BigDecimal, LocalDate, UUID, Enum, listas e entidades aninhadas).
- Builder de layout para arquivos completos, com posicoes absolutas, curingas e indices negativos.
- API simples para ida e volta: `toObject`, `toObjects`, `toLine`, `toLines`.

## O que vem a seguir
- Leia **Como usar** para ver um fluxo completo em menos de 5 minutos.
- Copie o snippet de dependencia em **Instalar via pom** e execute `mvn clean install`.
- Consulte a **FAQ** quando aparecer alguma duvida ou erro de conversao.
