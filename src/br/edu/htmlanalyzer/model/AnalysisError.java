package br.edu.htmlanalyzer.model;

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
        return String.format("Linha %d | %s | Tag: %s | %s",
                linha, tipo.getDescricao(), tag, mensagem);
    }
}
