package br.edu.htmlanalyzer.model;

/**
 * Classificação de uma tag HTML quanto ao seu papel estrutural.
 */
public enum TagType {
    ABERTURA("Abertura"),
    FECHAMENTO("Fechamento"),
    AUTOFECHAMENTO("Autofechamento");

    private final String descricao;

    TagType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
