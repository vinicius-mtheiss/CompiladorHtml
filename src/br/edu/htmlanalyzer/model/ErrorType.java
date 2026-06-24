package br.edu.htmlanalyzer.model;

/**
 * Tipos de erro detectados durante a validação estrutural do HTML.
 */
public enum ErrorType {
    TAG_FINAL_INESPERADA("Tag final inesperada"),
    TAG_FINAL_SEM_ABERTURA("Tag final sem tag inicial"),
    TAGS_NAO_FINALIZADAS("Tags não finalizadas"),
    TAG_MALFORMADA("Tag malformada");

    private final String descricao;

    ErrorType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
