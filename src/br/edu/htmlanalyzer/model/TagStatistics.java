package br.edu.htmlanalyzer.model;

/**
 * SUMÁRIO DO ARQUIVO: representa a estatística final de uma tag (nome,
 * frequência, tipo e primeira linha em que apareceu).
 * POR QUE ESTÁ SEPARADO: é um objeto de dados que transporta o resultado do
 * serviço de estatísticas até o relatório e a tabela da interface.
 */

/**
 * Estatísticas consolidadas de uma tag HTML.
 */
public class TagStatistics implements Comparable<TagStatistics> {

    // Guarda o nome normalizado da tag representada nesta linha de estatística.
    private final String tag;
    // Registra o número de aberturas ou singletons encontrados para a tag.
    private final int frequencia;
    // Informa o papel estrutural registrado para a tag.
    private final TagType tipo;
    // Guarda a primeira linha do arquivo em que essa tag apareceu.
    private final int primeiraOcorrencia;

    // Recebe todos os valores já consolidados pelo serviço de estatísticas.
    public TagStatistics(String tag, int frequencia, TagType tipo, int primeiraOcorrencia) {
        this.tag = tag;
        this.frequencia = frequencia;
        this.tipo = tipo;
        this.primeiraOcorrencia = primeiraOcorrencia;
    }

    public String getTag() {
        // Devolve o nome que identificará a tag no relatório.
        return tag;
    }

    public int getFrequencia() {
        // Devolve a contagem calculada para a tag.
        return frequencia;
    }

    public TagType getTipo() {
        // Devolve a classificação estrutural associada.
        return tipo;
    }

    public int getPrimeiraOcorrencia() {
        // Devolve a linha que ajuda o usuário a localizar a primeira ocorrência.
        return primeiraOcorrencia;
    }

    @Override
    public int compareTo(TagStatistics outra) {
        // Define a ordem alfabética sem diferenciar letras maiúsculas de minúsculas.
        return this.tag.compareToIgnoreCase(outra.tag);
    }
}
