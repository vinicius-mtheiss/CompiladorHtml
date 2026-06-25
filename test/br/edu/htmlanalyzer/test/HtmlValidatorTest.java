package br.edu.htmlanalyzer.test;

/**
 * SUMÁRIO DO ARQUIVO: testa parser e validador para documentos válidos,
 * malformados, multilinha, atributos, mensagens e combinações de erros.
 * POR QUE ESTÁ SEPARADO: regras de HTML têm muitos casos de borda; reuni-los
 * aqui documenta e protege o contrato esperado sem poluir as classes de produção.
 */

import br.edu.htmlanalyzer.datastructure.Lista;
import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.ErrorType;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.parser.HtmlTagParser;
import br.edu.htmlanalyzer.parser.HtmlValidator;

/**
 * Casos de teste para parser e validador HTML.
 */
public class HtmlValidatorTest {

    // Acumula as asserções que encontraram um resultado diferente do esperado.
    private static int falhas = 0;

    // Executa todos os cenários de parsing e validação estrutural.
    public static void main(String... args) {
        // Cada chamada cobre uma regra ou caso de borda importante do HTML aceito.
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

        // Resume o resultado da classe no terminal para quem executa a suíte.
        System.out.println("HtmlValidatorTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // Garante que um aninhamento HTML correto não produz erro.
    private static void testarDocumentoValido() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<body>").com("<div>").com("<p>Texto</p>")
                .com("</div>").com("</body>").com("</html>").fim();
        HtmlTagParser parser = new HtmlTagParser();
        HtmlValidator validator = new HtmlValidator();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(), "Documento válido");
    }

    // Garante que fechar uma tag diferente da abertura pendente produz o tipo correto.
    private static void testarTagFinalInesperada() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<body>").com("<div>").com("</span>")
                .com("</div>").com("</body>").com("</html>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tag final inesperada");
        assertEquals(ErrorType.TAG_FINAL_INESPERADA, erros.get(0).getTipo(), "Tipo correto");
    }

    // Garante que uma tag final isolada é identificada como sem abertura correspondente.
    private static void testarTagFinalSemAbertura() {
        Lista<String> linhas = linhas().com("<br>").com("</section>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tag final sem abertura");
        assertEquals(ErrorType.TAG_FINAL_SEM_ABERTURA, erros.get(0).getTipo(), "Tipo correto");
    }

    // Garante que o fim prematuro do arquivo deixa pendências detectáveis.
    private static void testarTagsNaoFinalizadas() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<body>").com("<div>").com("<p>Texto").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tags não finalizadas");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(0).getTipo(), "Tipo correto");
    }

    // Garante que a ausência de '>' gera a categoria de sintaxe malformada.
    private static void testarTagMalformada() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<body>").com("<div class=\"teste\"")
                .com("</body>").com("</html>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(!erros.isEmpty(), "Deve detectar tag malformada");
        assertEquals(ErrorType.TAG_MALFORMADA, erros.get(0).getTipo(), "Tipo correto");
    }

    // Garante que elementos singleton não criam exigência artificial de fechamento.
    private static void testarSingletonNaoEmpilhada() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<head>").com("<meta charset=\"UTF-8\">").com("<link rel=\"stylesheet\">")
                .com("</head>").com("<body>").com("<br>").com("<img src=\"a.png\">")
                .com("</body>").com("</html>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Singletons não devem causar erro");
    }

    // Garante que nomes HTML equivalem independentemente de maiúsculas e minúsculas.
    private static void testarCaseInsensitive() {
        Lista<String> linhas = linhas()
                .com("<HTML>").com("<Body>").com("<DIV>")
                .com("</div>").com("</BODY>").com("</html>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Tags case-insensitive");
    }

    // Garante que atributos não alteram o nome estrutural usado na validação.
    private static void testarAtributosIgnorados() {
        Lista<ParsedTag> tags = new Lista<>();
        tags.add(new ParsedTag("div", TagType.ABERTURA, 1, "<div class=\"x\">"));
        tags.add(new ParsedTag("div", TagType.FECHAMENTO, 2, "</div>"));
        HtmlValidator validator = new HtmlValidator();
        assertTrue(validator.validar(tags).isEmpty(), "Atributos ignorados na validação");
    }

    // Garante que linhas vazias não alteram a linha apresentada em mensagens de erro.
    private static void testarNumeracaoComLinhaEmBranco() {
        Lista<String> linhas = linhas().com("<html>").com("").com("</body>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertTrue(erros.size() >= 1, "Deve detectar ao menos um erro");
        assertEquals(3, erros.get(0).getLinha(), "Linha em branco não altera numeração");
        assertTrue(erros.get(0).getMensagem().contains("era esperada a tag final </html>"),
                "Mensagem de tag final inesperada");
    }

    // Garante que o símbolo '>' dentro de aspas não encerra a tag antes da hora.
    private static void testarAtributoComSinalDeMaior() {
        Lista<String> linhas = linhas().com("<div data-text=\"a > b\"></div>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Atributos com > entre aspas são reconhecidos");
    }

    // Garante que uma tag distribuída por várias linhas ainda pode ser reconhecida.
    private static void testarTagMultilinha() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<body>").com("<form>")
                .com("<input")
                .com("type=\"text\"")
                .com("id=\"titulo\"")
                .com("required")
                .com("placeholder=\" \"")
                .com(">")
                .com("<input type=\"radio\" name=\"status\" value=\"Lido\">")
                .com("</form>").com("</body>").com("</html>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        assertTrue(validator.validar(parser.extrairTags(linhas)).isEmpty(),
                "Tags input em múltiplas linhas devem ser reconhecidas");
    }

    // Garante o comportamento esperado quando há uma abertura sem fechamento antes de outra final.
    private static void testarTagAbertaNaoFechadaSemCascata() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<body>").com("<form>").com("<ul>").com("<li>").com("</li>")
                .com("</form>").com("</body>").com("</html>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertEquals(1, erros.size(), "Deve retornar apenas um erro");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(0).getTipo(), "Tipo correto");
        assertEquals(
                "Erro: Faltam tags finais no arquivo. Tags esperadas: </ul>",
                erros.get(0).toString(),
                "Mensagem padronizada de tag não finalizada");
    }

    // Garante que cada abertura pendente relevante seja relatada ao final do processamento.
    private static void testarVariasTagsAbertasNaoFechadas() {
        Lista<String> linhas = linhas()
                .com("<html>").com("<body>").com("<form>").com("<ul>")
                .com("<ul>").com("</form>").com("</body>").com("</html>").fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));
        assertEquals(2, erros.size(), "Deve retornar um erro para cada ul não fechada");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(0).getTipo(), "Primeiro erro");
        assertEquals(ErrorType.TAGS_NAO_FINALIZADAS, erros.get(1).getTipo(), "Segundo erro");
        assertTrue(erros.get(0).getMensagem().contains("Tags esperadas: </ul>"),
                "Erro aponta ul");
        assertTrue(erros.get(1).getMensagem().contains("Tags esperadas: </ul>"),
                "Erro aponta ul");
    }

    // Confere literalmente as mensagens que formam o contrato exibido ao usuário.
    private static void testarMensagensPadronizadas() {
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();

        Lista<AnalysisError> inesperada = validator.validar(parser.extrairTags(
                linhas().com("<p>").com("</body>").fim()
        ));
        assertEquals(
                "Erro na linha 2: Foi encontrada a tag final </body>, mas era esperada a tag final </p>.",
                inesperada.get(0).toString(),
                "Mensagem de tag final inesperada");

        Lista<AnalysisError> semAbertura = validator.validar(parser.extrairTags(
                linhas().com("</p>").fim()
        ));
        assertEquals(
                "Erro na linha 1: Foi encontrada a tag final </p>, mas não existe tag inicial correspondente.",
                semAbertura.get(0).toString(),
                "Mensagem de tag final sem tag inicial");

        Lista<AnalysisError> naoFinalizadas = validator.validar(parser.extrairTags(
                linhas().com("<p>Texto").fim()
        ));
        assertEquals(
                "Erro: Faltam tags finais no arquivo. Tags esperadas: </p>",
                naoFinalizadas.get(0).toString(),
                "Mensagem de tags não finalizadas");

        Lista<AnalysisError> malformada = validator.validar(parser.extrairTags(
                linhas().com("<html>").com("<div class=\"teste\"").com("</html>").fim()
        ));
        assertEquals(
                "Erro na linha 2: Foi encontrada uma tag malformada.",
                malformada.get(0).toString(),
                "Mensagem de tag malformada");
    }

    // Confirma que mais de um diagnóstico pode ser acumulado no mesmo documento.
    private static void testarMultiplosErrosNoMesmoArquivo() {
        Lista<String> linhas = linhas()
                .com("<ul")
                .com("")
                .com("<nav>")
                .com("    <ul>")
                .com("")
                .com("<div>")
                .com("    <ul>")
                .com("</div>")
                .com("</ul>")
                .fim();
        HtmlValidator validator = new HtmlValidator();
        HtmlTagParser parser = new HtmlTagParser();
        Lista<AnalysisError> erros = validator.validar(parser.extrairTags(linhas));

        assertTrue(erros.size() > 1, "Deve acumular múltiplos erros no mesmo arquivo");
        assertEquals(ErrorType.TAG_MALFORMADA, erros.get(0).getTipo(), "Primeiro erro: malformada");
        assertTrue(contemErroDoTipo(erros, ErrorType.TAGS_NAO_FINALIZADAS),
                "Deve incluir tags não finalizadas");
        assertTrue(contemMensagem(erros, "Tags esperadas: </ul>"),
                "Deve apontar ul não finalizada");
        assertTrue(contemMensagem(erros, "Tags esperadas: </nav>"),
                "Deve apontar nav não finalizada");
    }

    // Procura um trecho de mensagem na lista própria de erros para validar conteúdo textual relevante.
    private static boolean contemMensagem(Lista<AnalysisError> erros, String trecho) {
        for (AnalysisError erro : erros) {
            if (erro.getMensagem().contains(trecho)) {
                return true;
            }
        }
        return false;
    }

    // Procura um tipo de erro específico na lista própria de diagnósticos produzida.
    private static boolean contemErroDoTipo(Lista<AnalysisError> erros, ErrorType tipo) {
        for (AnalysisError erro : erros) {
            if (erro.getTipo() == tipo) {
                return true;
            }
        }
        return false;
    }

    // Cria um montador fluente de linhas sem usar estruturas prontas.
    private static LinhasBuilder linhas() {
        return new LinhasBuilder();
    }

    // Pequeno auxiliar de teste para deixar os cenários legíveis com a Lista própria.
    private static class LinhasBuilder {

        // Guarda as linhas adicionadas para o cenário atual.
        private final Lista<String> linhas = new Lista<>();

        // Adiciona uma linha e devolve o próprio builder para permitir encadeamento.
        LinhasBuilder com(String linha) {
            linhas.add(linha);
            return this;
        }

        // Finaliza o builder devolvendo a lista usada pelo parser.
        Lista<String> fim() {
            return linhas;
        }
    }

    // Registra falha quando uma condição booleana do cenário é falsa.
    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }

    // Compara valores de forma segura para null e registra detalhes quando houver divergência.
    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }
}
