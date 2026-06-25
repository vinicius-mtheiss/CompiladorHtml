package br.edu.htmlanalyzer.test;

/**
 * SUMÁRIO DO ARQUIVO: testa o fluxo completo com arquivos HTML reais da pasta
 * resources/samples, do serviço até o objeto de resultado.
 * POR QUE ESTÁ SEPARADO: diferente dos testes unitários, este confirma que as
 * classes especializadas funcionam juntas e que os exemplos do projeto batem.
 */

import br.edu.htmlanalyzer.model.AnalysisResult;
import br.edu.htmlanalyzer.service.HtmlAnalyzerService;

import java.io.File;

/**
 * Testes de integração com arquivos de exemplo.
 */
public class IntegrationTest {

    // Acumula falhas detectadas nos cenários que usam arquivos reais.
    private static int falhas = 0;

    // Executa os arquivos de amostra e as regras que integram todas as camadas do sistema.
    public static void main(String... args) throws Exception {
        // Centraliza a pasta para que os nomes dos fixtures sejam mais legíveis abaixo.
        String base = "resources/samples/";
        testarArquivoValido(base + "valido.html");
        testarArquivoInvalido(base + "tag_final_inesperada.html");
        testarArquivoInvalido(base + "tag_sem_abertura.html");
        testarArquivoInvalido(base + "tags_nao_finalizadas.html");
        testarArquivoInvalido(base + "tag_malformada.html");
        testarHierarquiaApenasQuandoValido(base + "valido.html");
        testarFrequenciaSemFechamentos(base + "valido.html");

        System.out.println("IntegrationTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // Confirma que o fluxo completo produz todas as saídas para um HTML correto.
    private static void testarArquivoValido(String caminho) throws Exception {
        HtmlAnalyzerService service = new HtmlAnalyzerService();
        AnalysisResult resultado = service.analisar(new File(caminho).getPath());
        assertTrue(resultado.isValido(), "Arquivo válido: " + caminho);
        assertTrue(!resultado.getEstatisticas().isEmpty(), "Estatísticas geradas");
        assertTrue(resultado.getRaiz() != null, "Hierarquia gerada");
    }

    // Confirma que um HTML incorreto produz erros e não gera árvore enganosa.
    private static void testarArquivoInvalido(String caminho) throws Exception {
        HtmlAnalyzerService service = new HtmlAnalyzerService();
        AnalysisResult resultado = service.analisar(new File(caminho).getPath());
        assertTrue(!resultado.isValido(), "Arquivo inválido: " + caminho);
        assertTrue(!resultado.getErros().isEmpty(), "Erros detectados");
        assertTrue(resultado.getRaiz() == null, "Sem hierarquia para inválido");
    }

    // Confirma que as tags aninhadas podem ser encontradas na representação da árvore.
    private static void testarHierarquiaApenasQuandoValido(String caminho) throws Exception {
        HtmlAnalyzerService service = new HtmlAnalyzerService();
        AnalysisResult resultado = service.analisar(new File(caminho).getPath());
        String hierarquia = resultado.getRaiz().gerarHierarquia(0);
        assertTrue(hierarquia.contains("html"), "Hierarquia contém html");
        assertTrue(hierarquia.contains("body"), "Hierarquia contém body");
        assertTrue(hierarquia.contains("div"), "Hierarquia contém div");
    }

    // Confirma que fechamentos não são contabilizados como novas ocorrências de uma tag.
    private static void testarFrequenciaSemFechamentos(String caminho) throws Exception {
        HtmlAnalyzerService service = new HtmlAnalyzerService();
        AnalysisResult resultado = service.analisar(new File(caminho).getPath());
        int frequenciaHtml = 0;
        for (br.edu.htmlanalyzer.model.TagStatistics estatistica : resultado.getEstatisticas()) {
            if ("html".equals(estatistica.getTag())) {
                frequenciaHtml = estatistica.getFrequencia();
                break;
            }
        }
        assertTrue(frequenciaHtml == 1, "Fechamentos não entram na frequência");

        int somaFrequencias = 0;
        for (br.edu.htmlanalyzer.model.TagStatistics estatistica : resultado.getEstatisticas()) {
            somaFrequencias += estatistica.getFrequencia();
        }
        assertTrue(resultado.getTotalTags() == somaFrequencias,
                "Total de tags deve coincidir com a soma das frequências");
    }

    // Registra uma falha quando a condição passada pelo cenário é falsa.
    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }
}
