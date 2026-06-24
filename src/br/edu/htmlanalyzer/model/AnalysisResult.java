package br.edu.htmlanalyzer.model;

/**
 * SUMÁRIO DO ARQUIVO: reúne tudo que uma análise produziu e sabe gerar o
 * relatório textual exibido ao usuário.
 * POR QUE ESTÁ SEPARADO: mantém o resultado coeso e evita que o serviço de
 * análise ou a interface precisem conhecer detalhes da apresentação textual.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado consolidado da análise de um documento HTML.
 */
public class AnalysisResult {

    // Caminho informado pelo usuário, usado para identificar o arquivo no relatório.
    private final String caminhoArquivo;
    // Indica se a validação terminou sem nenhum erro.
    private final boolean valido;
    // Guarda os diagnósticos na ordem em que foram encontrados.
    private final List<AnalysisError> erros;
    // Guarda as estatísticas disponíveis apenas para documentos válidos.
    private final List<TagStatistics> estatisticas;
    // Guarda a raiz da árvore disponível apenas para documentos válidos.
    private final HtmlNode raiz;
    // Registra a quantidade de linhas lidas para o resumo.
    private final int totalLinhas;
    // Registra a quantidade de tags contabilizadas para o resumo.
    private final int totalTags;

    public AnalysisResult(String caminhoArquivo,
                          boolean valido,
                          List<AnalysisError> erros,
                          List<TagStatistics> estatisticas,
                          HtmlNode raiz,
                          int totalLinhas,
                          int totalTags) {
        // Preserva o caminho que originou estes dados.
        this.caminhoArquivo = caminhoArquivo;
        // Preserva o veredito calculado pelo serviço.
        this.valido = valido;
        // Copia os erros para impedir que uma lista externa altere o resultado depois de criado.
        this.erros = new ArrayList<>(erros);
        // Substitui estatísticas ausentes por lista vazia, evitando null para quem consultar o resultado.
        this.estatisticas = estatisticas == null
                ? Collections.emptyList()
                : new ArrayList<>(estatisticas);
        // Mantém a referência da árvore já construída pelo serviço especializado.
        this.raiz = raiz;
        // Armazena o total de linhas lidas.
        this.totalLinhas = totalLinhas;
        // Armazena o total de tags aceitas na contagem.
        this.totalTags = totalTags;
    }

    public String getCaminhoArquivo() {
        // Expõe o caminho associado ao resultado.
        return caminhoArquivo;
    }

    public boolean isValido() {
        // Expõe o veredito sem recalcular a validação.
        return valido;
    }

    public List<AnalysisError> getErros() {
        // Impede que chamadores alterem a coleção interna de diagnósticos.
        return Collections.unmodifiableList(erros);
    }

    public List<TagStatistics> getEstatisticas() {
        // Impede que chamadores alterem as estatísticas guardadas no resultado.
        return Collections.unmodifiableList(estatisticas);
    }

    public HtmlNode getRaiz() {
        // Devolve a raiz da hierarquia ou null quando o documento é inválido.
        return raiz;
    }

    public int getTotalLinhas() {
        // Devolve o total usado no cabeçalho do relatório.
        return totalLinhas;
    }

    public int getTotalTags() {
        // Devolve o total de aberturas e singletons válidos encontrados.
        return totalTags;
    }

    public String gerarRelatorio() {
        // Monta todas as linhas do relatório eficientemente em um único acumulador.
        StringBuilder relatorio = new StringBuilder();
        // Escreve o título que identifica o documento apresentado.
        relatorio.append("=== RELATÓRIO DE ANÁLISE HTML ===").append(System.lineSeparator());
        relatorio.append("Arquivo: ").append(caminhoArquivo).append(System.lineSeparator());
        relatorio.append("Linhas processadas: ").append(totalLinhas).append(System.lineSeparator());
        relatorio.append("Total de tags (abertura/autofechamento): ").append(totalTags).append(System.lineSeparator());
        relatorio.append("Status: ").append(valido ? "VÁLIDO" : "INVÁLIDO").append(System.lineSeparator());
        // Deixa uma linha visual em branco antes da seção dependente do resultado.
        relatorio.append(System.lineSeparator());

        // Um documento inválido mostra apenas os erros, pois estatísticas e hierarquia seriam pouco confiáveis.
        if (!valido) {
            relatorio.append("--- ERROS ENCONTRADOS ---").append(System.lineSeparator());
            // Acrescenta cada diagnóstico completo em sua própria linha.
            for (AnalysisError erro : erros) {
                relatorio.append("(").append(erro.getTipo().getDescricao()).append(") ");
                relatorio.append(erro).append(System.lineSeparator());
            }
            // Encerra cedo porque não existe seção válida de estatísticas para este caso.
            return relatorio.toString();
        }

        // Inicia a seção exibida somente quando a validação foi bem-sucedida.
        relatorio.append("--- ESTATÍSTICAS DE TAGS ---").append(System.lineSeparator());
        relatorio.append(String.format("%-15s %-12s %-18s %s",
                "Tag", "Frequência", "Tipo", "1ª Ocorrência")).append(System.lineSeparator());
        // Escreve uma linha formatada para cada tag ordenada.
        for (TagStatistics estatistica : estatisticas) {
            relatorio.append(String.format("%-15s %-12d %-18s %d",
                    estatistica.getTag(),
                    estatistica.getFrequencia(),
                    estatistica.getTipo().getDescricao(),
                    estatistica.getPrimeiraOcorrencia())).append(System.lineSeparator());
        }

        // Separa visualmente a tabela da árvore textual.
        relatorio.append(System.lineSeparator());
        relatorio.append("--- HIERARQUIA DO DOCUMENTO ---").append(System.lineSeparator());
        // Só há árvore quando o construtor de hierarquia recebeu um documento válido com tags utilizáveis.
        if (raiz != null) {
            relatorio.append(raiz.gerarHierarquia(0));
        // Caso um arquivo válido não gere nós, informa a condição de maneira amigável.
        } else {
            relatorio.append("Nenhuma hierarquia disponível.").append(System.lineSeparator());
        }

        // Materializa o texto acumulado para entrega à interface ou ao console.
        return relatorio.toString();
    }
}
