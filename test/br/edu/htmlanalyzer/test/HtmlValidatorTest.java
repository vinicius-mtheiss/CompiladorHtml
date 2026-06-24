package br.edu.htmlanalyzer.test;

import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.ErrorType;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.parser.HtmlTagParser;
import br.edu.htmlanalyzer.parser.HtmlValidator;

import java.util.Arrays;
import java.util.List;

/**
 * Casos de teste para parser e validador HTML.
 */
public class HtmlValidatorTest {

    private static int falhas = 0;

    public static void main(String[] args) {
        testarDocumentoValido();
        testarTagFinalInesperada();
        testarTagFinalSemAbertura();
        testarTagsNaoFinalizadas();
        testarTagMalformada();
        testarSingletonNaoEmpilhada();
        testarCaseInsensitive();
        testarAtributosIgnorados();
        testarAtributoComSinalDeMaior();
        testarNumeracaoComLinhaEmBranco();
        testarTagMultilinha();
        testarTagAbertaNaoFechadaSemCascata();
        testarVariasTagsAbertasNaoFechadas();
        testarMensagensPadronizadas();
        testarMultiplosErrosNoMesmoArquivo();

        System.out.println("HtmlValidatorTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void testarDocumentoValido() {
        List<String> linhas = Arrays.asList(
                "<html>", "<body>", "<div>", "<p>Texto</p>", "</div>", "</body>", "</html>"
        );
        HtmlTagParser parser = new HtmlTagParser();
        HtmlValidator validator = new HtmlValidator();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(), "Documento válido");
    }

    private static void testarTagFinalInesperada() {
        List<String> linhas = Arrays.asList(
                "<html>", "<body>", "<div>", "</span>", "</div>", "</body>", "</html>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tag final inesperada");
        assertEquals(ErrorType.TAG_FINAL_INESPERADA, erros.get(0).getTipo(), "Tipo correto");
    }

    private static void testarTagFinalSemAbertura() {
        List<String> linhas = Arrays.asList(
                "<br>", "</section>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tag final sem abertura");
        assertEquals(ErrorType.TAG_FINAL_SEM_ABERTURA, erros.get(0).getTipo(), "Tipo correto");
    }

    private static void testarTagsNaoFinalizadas() {
        List<String> linhas = Arrays.asList(
                "<html>", "<body>", "<div>", "<p>Texto"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tags não finalizadas");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(0).getTipo(), "Tipo correto");
    }

    private static void testarTagMalformada() {
        List<String> linhas = Arrays.asList(
                "<html>", "<body>", "<div class=\"teste\"", "</body>", "</html>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tag malformada");
        assertEquals(ErrorType.TAG_MALFORMADA, erros.get(0).getTipo(), "Tipo correto");
    }

    private static void testarSingletonNaoEmpilhada() {
        List<String> linhas = Arrays.asList(
                "<html>", "<head>", "<meta charset=\"UTF-8\">", "<link rel=\"stylesheet\">",
                "</head>", "<body>", "<br>", "<img src=\"a.png\">", "</body>", "</html>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Singletons não devem causar erro");
    }

    private static void testarCaseInsensitive() {
        List<String> linhas = Arrays.asList(
                "<HTML>", "<Body>", "<DIV>", "</div>", "</BODY>", "</html>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Tags case-insensitive");
    }

    private static void testarAtributosIgnorados() {
        List<ParsedTag> tags = Arrays.asList(
                new ParsedTag("div", TagType.ABERTURA, 1, "<div class=\"x\">"),
                new ParsedTag("div", TagType.FECHAMENTO, 2, "</div>")
        );
        HtmlValidator validator = new HtmlValidator();
        assertTrue(validator.validar(tags).isEmpty(), "Atributos ignorados na validação");
    }

    private static void testarNumeracaoComLinhaEmBranco() {
        List<String> linhas = Arrays.asList("<html>", "", "</body>");
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(erros.size() >= 1, "Deve detectar ao menos um erro");
        assertEquals(3, erros.get(0).getLinha(), "Linha em branco não altera numeração");
        assertTrue(erros.get(0).getMensagem().contains("era esperada a tag final </html>"),
                "Mensagem de tag final inesperada");
    }

    private static void testarAtributoComSinalDeMaior() {
        List<String> linhas = Arrays.asList("<div data-text=\"a > b\"></div>");
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Atributos com > entre aspas são reconhecidos");
    }

    private static void testarTagMultilinha() {
        List<String> linhas = Arrays.asList(
                "<html>", "<body>", "<form>",
                "<input",
                "type=\"text\"",
                "id=\"titulo\"",
                "required",
                "placeholder=\" \"",
                ">",
                "<input type=\"radio\" name=\"status\" value=\"Lido\">",
                "</form>", "</body>", "</html>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Tags input em múltiplas linhas devem ser reconhecidas");
    }

    private static void testarTagAbertaNaoFechadaSemCascata() {
        List<String> linhas = Arrays.asList(
                "<html>", "<body>", "<form>", "<ul>", "<li>", "</li>",
                "</form>", "</body>", "</html>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertEquals(1, erros.size(), "Deve retornar apenas um erro");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(0).getTipo(), "Tipo correto");
        assertEquals(
                "Erro: Faltam tags finais no arquivo. Tags esperadas: </ul>",
                erros.get(0).toString(),
                "Mensagem padronizada de tag não finalizada");
    }

    private static void testarVariasTagsAbertasNaoFechadas() {
        List<String> linhas = Arrays.asList(
                "<html>", "<body>", "<form>", "<ul>", "<ul>", "</form>", "</body>", "</html>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertEquals(2, erros.size(), "Deve retornar um erro para cada ul não fechada");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(0).getTipo(), "Primeiro erro");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(1).getTipo(), "Segundo erro");
        assertTrue(erros.get(0).getMensagem().contains("Tags esperadas: </ul>"),
                "Erro aponta ul");
        assertTrue(erros.get(1).getMensagem().contains("Tags esperadas: </ul>"),
                "Erro aponta ul");
    }

    private static void testarMensagensPadronizadas() {
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();

        List<AnalysisError> inesperada = validator.validar(parser.extrairTags(Arrays.asList(
                "<p>", "</body>"
        )));
        assertEquals(
                "Erro na linha 2: Foi encontrada a tag final </body>, mas era esperada a tag final </p>.",
                inesperada.get(0).toString(),
                "Mensagem de tag final inesperada");

        List<AnalysisError> semAbertura = validator.validar(parser.extrairTags(Arrays.asList(
                "</p>"
        )));
        assertEquals(
                "Erro na linha 1: Foi encontrada a tag final </p>, mas não existe tag inicial correspondente.",
                semAbertura.get(0).toString(),
                "Mensagem de tag final sem tag inicial");

        List<AnalysisError> naoFinalizadas = validator.validar(parser.extrairTags(Arrays.asList(
                "<p>Texto"
        )));
        assertEquals(
                "Erro: Faltam tags finais no arquivo. Tags esperadas: </p>",
                naoFinalizadas.get(0).toString(),
                "Mensagem de tags não finalizadas");

        List<AnalysisError> malformada = validator.validar(parser.extrairTags(Arrays.asList(
                "<html>", "<div class=\"teste\"", "</html>"
        )));
        assertEquals(
                "Erro na linha 2: Foi encontrada uma tag malformada.",
                malformada.get(0).toString(),
                "Mensagem de tag malformada");
    }

    private static void testarMultiplosErrosNoMesmoArquivo() {
        List<String> linhas = Arrays.asList(
                "<ul",
                "",
                "<nav>",
                "    <ul>",
                "",
                "<div>",
                "    <ul>",
                "</div>",
                "</ul>"
        );
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        List<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));

        assertTrue(erros.size() > 1, "Deve acumular múltiplos erros no mesmo arquivo");
        assertEquals(ErrorType.TAG_MALFORMADA, erros.get(0).getTipo(), "Primeiro erro: malformada");
        assertTrue(contemErroDoTipo(erros, ErrorType.TAGS_NAO_FINALIZADAS),
                "Deve incluir tags não finalizadas");
        assertTrue(contemMensagem(erros, "Tags esperadas: </ul>"),
                "Deve apontar ul não finalizada");
        assertTrue(contemMensagem(erros, "Tags esperadas: </nav>"),
                "Deve apontar nav não finalizada");
    }

    private static boolean contemMensagem(List<AnalysisError> erros, String trecho) {
        for (AnalysisError erro : erros) {
            if (erro.getMensagem().contains(trecho)) {
                return true;
            }
        }
        return false;
    }

    private static boolean contemErroDoTipo(List<AnalysisError> erros, ErrorType tipo) {
        for (AnalysisError erro : erros) {
            if (erro.getTipo() == tipo) {
                return true;
            }
        }
        return false;
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }

    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }
}
