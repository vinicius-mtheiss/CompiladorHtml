package br.edu.htmlanalyzer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado consolidado da análise de um documento HTML.
 */
public class AnalysisResult {

    private final String caminhoArquivo;
    private final boolean valido;
    private final List<AnalysisError> erros;
    private final List<TagStatistics> estatisticas;
    private final HtmlNode raiz;
    private final int totalLinhas;
    private final int totalTags;

    public AnalysisResult(String caminhoArquivo,
                          boolean valido,
                          List<AnalysisError> erros,
                          List<TagStatistics> estatisticas,
                          HtmlNode raiz,
                          int totalLinhas,
                          int totalTags) {
        this.caminhoArquivo = caminhoArquivo;
        this.valido = valido;
        this.erros = new ArrayList<>(erros);
        this.estatisticas = estatisticas == null
                ? Collections.emptyList()
                : new ArrayList<>(estatisticas);
        this.raiz = raiz;
        this.totalLinhas = totalLinhas;
        this.totalTags = totalTags;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public boolean isValido() {
        return valido;
    }

    public List<AnalysisError> getErros() {
        return Collections.unmodifiableList(erros);
    }

    public List<TagStatistics> getEstatisticas() {
        return Collections.unmodifiableList(estatisticas);
    }

    public HtmlNode getRaiz() {
        return raiz;
    }

    public int getTotalLinhas() {
        return totalLinhas;
    }

    public int getTotalTags() {
        return totalTags;
    }

    public String gerarRelatorio() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO DE ANÁLISE HTML ===").append(System.lineSeparator());
        relatorio.append("Arquivo: ").append(caminhoArquivo).append(System.lineSeparator());
        relatorio.append("Linhas processadas: ").append(totalLinhas).append(System.lineSeparator());
        relatorio.append("Total de tags: ").append(totalTags).append(System.lineSeparator());
        relatorio.append("Status: ").append(valido ? "VÁLIDO" : "INVÁLIDO").append(System.lineSeparator());
        relatorio.append(System.lineSeparator());

        if (!valido) {
            relatorio.append("--- ERROS ENCONTRADOS ---").append(System.lineSeparator());
            for (AnalysisError erro : erros) {
                relatorio.append(erro).append(System.lineSeparator());
            }
            return relatorio.toString();
        }

        relatorio.append("--- ESTATÍSTICAS DE TAGS ---").append(System.lineSeparator());
        relatorio.append(String.format("%-15s %-12s %-18s %s",
                "Tag", "Frequência", "Tipo", "1ª Ocorrência")).append(System.lineSeparator());
        for (TagStatistics estatistica : estatisticas) {
            relatorio.append(String.format("%-15s %-12d %-18s %d",
                    estatistica.getTag(),
                    estatistica.getFrequencia(),
                    estatistica.getTipo().getDescricao(),
                    estatistica.getPrimeiraOcorrencia())).append(System.lineSeparator());
        }

        relatorio.append(System.lineSeparator());
        relatorio.append("--- HIERARQUIA DO DOCUMENTO ---").append(System.lineSeparator());
        if (raiz != null) {
            relatorio.append(raiz.gerarHierarquia(0));
        } else {
            relatorio.append("Nenhuma hierarquia disponível.").append(System.lineSeparator());
        }

        return relatorio.toString();
    }
}
