package br.edu.htmlanalyzer.test;

import br.edu.htmlanalyzer.model.AnalysisResult;
import br.edu.htmlanalyzer.service.HtmlAnalyzerService;

import java.io.File;

/**
 * Testes de integração com arquivos de exemplo.
 */
public class IntegrationTest {

    private static int falhas = 0;

    public static void main(String[] args) throws Exception {
        String base = "resources/samples/";
        testarArquivoValido(base + "valido.html");
        testarArquivoInvalido(base + "tag_final_inesperada.html");
        testarArquivoInvalido(base + "tag_sem_abertura.html");
        testarArquivoInvalido(base + "tags_nao_finalizadas.html");
        testarArquivoInvalido(base + "tag_malformada.html");
        testarHierarquiaApenasQuandoValido(base + "valido.html");

        System.out.println("IntegrationTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void testarArquivoValido(String caminho) throws Exception {
        HtmlAnalyzerService service = new HtmlAnalyzerService();
        AnalysisResult resultado = service.analisar(new File(caminho).getPath());
        assertTrue(resultado.isValido(), "Arquivo válido: " + caminho);
        assertTrue(!resultado.getEstatisticas().isEmpty(), "Estatísticas geradas");
        assertTrue(resultado.getRaiz() != null, "Hierarquia gerada");
    }

    private static void testarArquivoInvalido(String caminho) throws Exception {
        HtmlAnalyzerService service = new HtmlAnalyzerService();
        AnalysisResult resultado = service.analisar(new File(caminho).getPath());
        assertTrue(!resultado.isValido(), "Arquivo inválido: " + caminho);
        assertTrue(!resultado.getErros().isEmpty(), "Erros detectados");
        assertTrue(resultado.getRaiz() == null, "Sem hierarquia para inválido");
    }

    private static void testarHierarquiaApenasQuandoValido(String caminho) throws Exception {
        HtmlAnalyzerService service = new HtmlAnalyzerService();
        AnalysisResult resultado = service.analisar(new File(caminho).getPath());
        String hierarquia = resultado.getRaiz().gerarHierarquia(0);
        assertTrue(hierarquia.contains("html"), "Hierarquia contém html");
        assertTrue(hierarquia.contains("body"), "Hierarquia contém body");
        assertTrue(hierarquia.contains("div"), "Hierarquia contém div");
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }
}
