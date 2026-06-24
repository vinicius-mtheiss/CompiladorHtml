package br.edu.htmlanalyzer.model;

/**
 * SUMÁRIO DO ARQUIVO: representa um nó da árvore do documento HTML e sabe
 * transformar sua subárvore em texto indentado.
 * POR QUE ESTÁ SEPARADO: a estrutura da árvore é independente tanto da forma
 * de montá-la quanto da interface que a exibe, facilitando reutilização.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nó da árvore hierárquica do documento HTML.
 */
public class HtmlNode {

    // Nome normalizado da tag que este nó representa.
    private final String tag;
    // Referência ao elemento que contém este nó; é nula na raiz.
    private final HtmlNode pai;
    // Lista mutável interna dos elementos diretamente contidos nesta tag.
    private final List<HtmlNode> filhos;

    // Cria um nó e prepara sua lista de filhos inicialmente vazia.
    public HtmlNode(String tag, HtmlNode pai) {
        this.tag = tag;
        this.pai = pai;
        this.filhos = new ArrayList<>();
    }

    public String getTag() {
        // Permite consultar o nome exibido na hierarquia.
        return tag;
    }

    public HtmlNode getPai() {
        // Permite navegar da tag atual para sua tag contenedora.
        return pai;
    }

    public List<HtmlNode> getFilhos() {
        // Retorna uma visão somente leitura para impedir alterações externas inconsistentes.
        return Collections.unmodifiableList(filhos);
    }

    // Adiciona um filho durante a construção controlada da árvore.
    public void adicionarFilho(HtmlNode filho) {
        filhos.add(filho);
    }

    /**
     * Gera representação textual indentada da hierarquia.
     */
    public String gerarHierarquia(int nivel) {
        // Acumula o texto em memória sem criar muitas strings intermediárias.
        StringBuilder builder = new StringBuilder();
        // Cada nível acrescenta quatro espaços para tornar a relação pai-filho visível.
        for (int i = 0; i < Math.max(0, nivel); i++) {
            builder.append("    ");
        }
        // Escreve o nome deste próprio nó após sua indentação.
        builder.append(tag);
        // Finaliza a linha usando o separador adequado ao sistema operacional.
        builder.append(System.lineSeparator());

        // Pede recursivamente a representação de cada filho um nível mais fundo.
        for (HtmlNode filho : filhos) {
            builder.append(filho.gerarHierarquia(nivel + 1));
        }
        // Entrega o texto completo desta subárvore.
        return builder.toString();
    }
}
