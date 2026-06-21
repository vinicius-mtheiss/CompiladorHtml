package br.edu.htmlanalyzer.model;

/**
 * Classificação de uma tag HTML quanto ao seu papel estrutural.
 */
public enum TagType {
    ABERTURA("Normal"),
    FECHAMENTO("Fechamento"),
    AUTOFECHAMENTO("Singleton");

    private final String descricao;

    TagType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
