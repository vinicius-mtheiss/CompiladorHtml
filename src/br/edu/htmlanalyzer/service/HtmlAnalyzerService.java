package br.edu.htmlanalyzer.service;

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

    private final HtmlTagParser parser;
    private final HtmlValidator validator;
    private final StatisticsService statisticsService;
    private final HierarchyBuilder hierarchyBuilder;

    public HtmlAnalyzerService() {
        this.parser = new HtmlTagParser();
        this.validator = new HtmlValidator();
        this.statisticsService = new StatisticsService();
        this.hierarchyBuilder = new HierarchyBuilder();
    }

    public AnalysisResult analisar(String caminhoArquivo) throws IOException {
        if (!FileReaderUtil.isExtensaoValida(caminhoArquivo)) {
            throw new IOException("Extensão inválida. Utilize arquivos .html, .htm ou .txt.");
        }

        List<String> linhas = FileReaderUtil.lerLinhas(caminhoArquivo);
        List<ParsedTag> tags = parser.extrairTags(linhas);
        List<AnalysisError> erros = validator.validar(tags);

        boolean valido = erros.isEmpty();
        List<TagStatistics> estatisticas = null;
        HtmlNode raiz = null;

        if (valido) {
            estatisticas = statisticsService.gerarEstatisticas(tags);
            raiz = hierarchyBuilder.construirHierarquia(tags);
        }

        return new AnalysisResult(
                caminhoArquivo,
                valido,
                erros,
                estatisticas,
                raiz,
                linhas.size(),
                tags.size()
        );
    }
}
