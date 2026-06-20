package br.edu.htmlanalyzer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nó da árvore hierárquica do documento HTML.
 */
public class HtmlNode {

    private final String tag;
    private final HtmlNode pai;
    private final List<HtmlNode> filhos;

    public HtmlNode(String tag, HtmlNode pai) {
        this.tag = tag;
        this.pai = pai;
        this.filhos = new ArrayList<>();
    }

    public String getTag() {
        return tag;
    }

    public HtmlNode getPai() {
        return pai;
    }

    public List<HtmlNode> getFilhos() {
        return Collections.unmodifiableList(filhos);
    }

    public void adicionarFilho(HtmlNode filho) {
        filhos.add(filho);
    }

    /**
     * Gera representação textual indentada da hierarquia.
     */
    public String gerarHierarquia(int nivel) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Math.max(0, nivel); i++) {
            builder.append("    ");
        }
        builder.append(tag);
        builder.append(System.lineSeparator());

        for (HtmlNode filho : filhos) {
            builder.append(filho.gerarHierarquia(nivel + 1));
        }
        return builder.toString();
    }
}
