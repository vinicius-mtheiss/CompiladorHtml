package br.edu.htmlanalyzer.model;

/**
 * Tag extraída do documento com metadados de posição.
 */
public class ParsedTag {

    private final String nome;
    private final TagType tipo;
    private final int linha;
    private final String original;

    public ParsedTag(String nome, TagType tipo, int linha, String original) {
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
        this.original = original;
    }

    public String getNome() {
        return nome;
    }

    public TagType getTipo() {
        return tipo;
    }

    public int getLinha() {
        return linha;
    }

    public String getOriginal() {
        return original;
    }
}
