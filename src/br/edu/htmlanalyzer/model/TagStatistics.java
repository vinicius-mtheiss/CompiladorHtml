package br.edu.htmlanalyzer.model;

/**
 * Estatísticas consolidadas de uma tag HTML.
 */
public class TagStatistics implements Comparable<TagStatistics> {

    private final String tag;
    private final int frequencia;
    private final TagType tipo;
    private final int primeiraOcorrencia;

    public TagStatistics(String tag, int frequencia, TagType tipo, int primeiraOcorrencia) {
        this.tag = tag;
        this.frequencia = frequencia;
        this.tipo = tipo;
        this.primeiraOcorrencia = primeiraOcorrencia;
    }

    public String getTag() {
        return tag;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public TagType getTipo() {
        return tipo;
    }

    public int getPrimeiraOcorrencia() {
        return primeiraOcorrencia;
    }

    @Override
    public int compareTo(TagStatistics outra) {
        return this.tag.compareToIgnoreCase(outra.tag);
    }
}
