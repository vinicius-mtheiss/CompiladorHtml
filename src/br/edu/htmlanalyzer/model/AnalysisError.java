package br.edu.htmlanalyzer.model;

import br.edu.htmlanalyzer.util.TagUtils;

/**
 * Representa um erro encontrado durante a análise do documento HTML.
 */
public class AnalysisError {

    private final ErrorType tipo;
    private final String mensagem;
    private final int linha;
    private final String tag;

    public AnalysisError(ErrorType tipo, String mensagem, int linha, String tag) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.linha = linha;
        this.tag = tag;
    }

    public static AnalysisError tagFinalInesperada(int linha, String encontrada, String esperada) {
        String tagEncontrada = TagUtils.normalizar(encontrada);
        String tagEsperada = TagUtils.normalizar(esperada);
        return new AnalysisError(
                ErrorType.TAG_FINAL_INESPERADA,
                String.format(
                        "Foi encontrada a tag final </%s>, mas era esperada a tag final </%s>.",
                        tagEncontrada, tagEsperada),
                linha,
                tagEncontrada
        );
    }

    public static AnalysisError tagFinalSemTagInicial(int linha, String tagEncontrada) {
        String tag = TagUtils.normalizar(tagEncontrada);
        return new AnalysisError(
                ErrorType.TAG_FINAL_SEM_ABERTURA,
                String.format(
                        "Foi encontrada a tag final </%s>, mas não existe tag inicial correspondente.",
                        tag),
                linha,
                tag
        );
    }

    public static AnalysisError tagsNaoFinalizadas(String tagEsperada) {
        String tag = TagUtils.normalizar(tagEsperada);
        return new AnalysisError(
                ErrorType.TAGS_NAO_FINALIZADAS,
                String.format("Faltam tags finais no arquivo. Tags esperadas: </%s>", tag),
                0,
                "</" + tag + ">"
        );
    }

    public static AnalysisError tagMalformada(int linha, String tagOriginal) {
        return new AnalysisError(
                ErrorType.TAG_MALFORMADA,
                "Foi encontrada uma tag malformada.",
                linha,
                tagOriginal
        );
    }

    public ErrorType getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public int getLinha() {
        return linha;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return linha > 0
                ? String.format("Erro na linha %d: %s", linha, mensagem)
                : "Erro: " + mensagem;
    }
}
