package br.edu.htmlanalyzer.model;

/**
 * SUMÁRIO DO ARQUIVO: enumera e nomeia as categorias de erro de validação.
 * POR QUE ESTÁ SEPARADO: centraliza os tipos aceitos para que validador,
 * testes e relatório usem os mesmos valores, sem comparações frágeis de texto.
 */

/**
 * Tipos de erro detectados durante a validação estrutural do HTML.
 */
public enum ErrorType {
    // Há uma tag final, mas ela não fecha a tag que está aberta mais recentemente.
    TAG_FINAL_INESPERADA("Tag final inesperada"),
    // Há uma tag final quando nenhuma abertura correspondente existe na pilha.
    TAG_FINAL_SEM_ABERTURA("Tag final sem tag inicial"),
    // O arquivo terminou deixando uma ou mais tags abertas.
    TAGS_NAO_FINALIZADAS("Tags não finalizadas"),
    // O parser não conseguiu reconhecer a sintaxe de uma tag.
    TAG_MALFORMADA("Tag malformada");

    // Texto legível para o cabeçalho de cada erro do relatório.
    private final String descricao;

    // Associa o texto descritivo a cada constante da enumeração.
    ErrorType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        // Expõe o rótulo sem modificar a constante.
        return descricao;
    }
}
