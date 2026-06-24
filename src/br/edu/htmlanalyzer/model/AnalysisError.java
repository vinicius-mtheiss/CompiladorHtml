package br.edu.htmlanalyzer.model;

/**
 * SUMÁRIO DO ARQUIVO: representa um erro de HTML com seu tipo, mensagem,
 * linha e tag relacionada, além de fábricas para mensagens padronizadas.
 * POR QUE ESTÁ SEPARADO: concentra a criação e a formatação dos erros para
 * que o validador cuide somente de detectar condições inválidas.
 */

import br.edu.htmlanalyzer.util.TagUtils;

/**
 * Representa um erro encontrado durante a análise do documento HTML.
 */
public class AnalysisError {

    // Categoria fixa que permite identificar o problema sem analisar a mensagem.
    private final ErrorType tipo;
    // Explicação legível que será exibida ao usuário.
    private final String mensagem;
    // Linha de origem; zero é usada quando o erro se refere ao fim do arquivo.
    private final int linha;
    // Nome ou trecho de tag associado ao diagnóstico.
    private final String tag;

    // Constrói um erro já completamente definido por uma das fábricas abaixo.
    public AnalysisError(ErrorType tipo, String mensagem, int linha, String tag) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.linha = linha;
        this.tag = tag;
    }

    public static AnalysisError tagFinalInesperada(int linha, String encontrada, String esperada) {
        // Padroniza o texto da tag recebida para uma mensagem consistente.
        String tagEncontrada = TagUtils.normalizar(encontrada);
        // Padroniza também a tag que o validador aguardava no topo da pilha.
        String tagEsperada = TagUtils.normalizar(esperada);
        // Cria o erro com o tipo, frase, linha e tag encontrados.
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
        // Normaliza a tag final que apareceu sem abertura correspondente.
        String tag = TagUtils.normalizar(tagEncontrada);
        // Monta o erro específico usando a mensagem definida como contrato do relatório.
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
        // Normaliza o nome da abertura que ficou pendente ao fim do arquivo.
        String tag = TagUtils.normalizar(tagEsperada);
        // A linha zero sinaliza que esse problema foi percebido somente no encerramento da análise.
        return new AnalysisError(
                ErrorType.TAGS_NAO_FINALIZADAS,
                String.format("Faltam tags finais no arquivo. Tags esperadas: </%s>", tag),
                0,
                "</" + tag + ">"
        );
    }

    public static AnalysisError tagMalformada(int linha, String tagOriginal) {
        // Cria um diagnóstico para sintaxe que o parser não conseguiu classificar.
        return new AnalysisError(
                ErrorType.TAG_MALFORMADA,
                "Foi encontrada uma tag malformada.",
                linha,
                tagOriginal
        );
    }

    public ErrorType getTipo() {
        // Entrega a categoria programática do erro.
        return tipo;
    }

    public String getMensagem() {
        // Entrega a frase pronta para apresentação.
        return mensagem;
    }

    public int getLinha() {
        // Entrega a linha para realçar o local do problema quando ela existir.
        return linha;
    }

    public String getTag() {
        // Entrega a tag relacionada ao diagnóstico.
        return tag;
    }

    @Override
    public String toString() {
        // Erros com linha são formatados de modo mais preciso; erros de fim de arquivo não têm linha específica.
        return linha > 0
                ? String.format("Erro na linha %d: %s", linha, mensagem)
                : "Erro: " + mensagem;
    }
}
