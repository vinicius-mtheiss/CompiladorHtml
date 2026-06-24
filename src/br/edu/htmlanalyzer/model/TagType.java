package br.edu.htmlanalyzer.model;

/**
 * SUMÁRIO DO ARQUIVO: define os três papéis estruturais possíveis de uma tag.
 * POR QUE ESTÁ SEPARADO: esse vocabulário é compartilhado por parser,
 * validador, estatísticas e interface, evitando textos e regras duplicados.
 */

/**
 * Classificação de uma tag HTML quanto ao seu papel estrutural.
 */
public enum TagType {
    // Representa <div>, <p> e outras tags que precisam de uma tag final correspondente.
    ABERTURA("Normal"),
    // Representa </div>, </p> e outras tags que encerram uma abertura anterior.
    FECHAMENTO("Fechamento"),
    // Representa tags sem conteúdo interno, como <br>, <img> ou <meta>.
    AUTOFECHAMENTO("Singleton");

    // Texto amigável que será mostrado no relatório e na interface gráfica.
    private final String descricao;

    // Recebe o rótulo associado a cada constante declarada acima.
    TagType(String descricao) {
        this.descricao = descricao;
    }

    // Expõe o rótulo sem permitir que código externo altere a enumeração.
    public String getDescricao() {
        return descricao;
    }
}
