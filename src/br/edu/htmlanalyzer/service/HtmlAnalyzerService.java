package br.edu.htmlanalyzer.service;

/**
 * SUMÁRIO DO ARQUIVO: coordena a leitura do arquivo, extração, validação e,
 * para documentos válidos, a geração de estatísticas e hierarquia.
 * POR QUE ESTÁ SEPARADO: funciona como fachada do caso de uso; a tela chama
 * uma operação simples sem depender das classes especializadas internas.
 */

import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.AnalysisResult;
import br.edu.htmlanalyzer.model.HtmlNode;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagStatistics;
import br.edu.htmlanalyzer.parser.HtmlTagParser;
import br.edu.htmlanalyzer.parser.HtmlValidator;
import br.edu.htmlanalyzer.util.FileReaderUtil;

import java.io.IOException;
import java.util.List;

/**
 * Orquestra leitura, parsing, validação, estatísticas e hierarquia.
 */
public class HtmlAnalyzerService {

    // Extrai os objetos ParsedTag a partir das linhas brutas do arquivo.
    private final HtmlTagParser parser;
    // Verifica se a sequência de tags extraídas respeita o aninhamento HTML.
    private final HtmlValidator validator;
    // Calcula totais e detalhes de cada tag quando o documento é válido.
    private final StatisticsService statisticsService;
    // Constrói a árvore pai-filho das tags válidas.
    private final HierarchyBuilder hierarchyBuilder;

    // Cria as dependências que formam as etapas da análise.
    public HtmlAnalyzerService() {
        this.parser = new HtmlTagParser();
        this.validator = new HtmlValidator();
        this.statisticsService = new StatisticsService();
        this.hierarchyBuilder = new HierarchyBuilder();
    }

    public AnalysisResult analisar(String caminhoArquivo) throws IOException {
        // Interrompe cedo se o arquivo não pertence a um formato aceito.
        if (!FileReaderUtil.isExtensaoValida(caminhoArquivo)) {
            throw new IOException("Extensão inválida. Utilize arquivos .html, .htm ou .txt.");
        }

        // Lê todas as linhas preservando sua ordem e numeração.
        List<String> linhas = FileReaderUtil.lerLinhas(caminhoArquivo);
        // Converte os trechos HTML do texto em objetos que carregam nome, tipo e linha.
        List<ParsedTag> tags = parser.extrairTags(linhas);
        // Acumula os erros estruturais encontrados na sequência de tags.
        List<AnalysisError> erros = validator.validar(tags);

        // Um documento só é válido se nenhuma regra de validação gerou erro.
        boolean valido = erros.isEmpty();
        // Começam nulas porque não são geradas para um documento inválido.
        List<TagStatistics> estatisticas = null;
        HtmlNode raiz = null;

        // Evita gerar resultados estruturais que poderiam ser enganosos para HTML incorreto.
        if (valido) {
            // Conta e ordena as tags que devem aparecer no relatório.
            estatisticas = statisticsService.gerarEstatisticas(tags);
            // Monta a relação de pais e filhos entre as tags.
            raiz = hierarchyBuilder.construirHierarquia(tags);
        }

        // Empacota todos os resultados, inclusive totais úteis para a interface.
        return new AnalysisResult(
                caminhoArquivo,
                valido,
                erros,
                estatisticas,
                raiz,
                linhas.size(),
                statisticsService.contarTags(tags)
        );
    }
}
