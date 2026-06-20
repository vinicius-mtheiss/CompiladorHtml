package br.edu.htmlanalyzer.service;

import br.edu.htmlanalyzer.datastructure.Stack;
import br.edu.htmlanalyzer.model.HtmlNode;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.List;

/**
 * Constrói a árvore hierárquica do documento HTML quando este é válido.
 */
public class HierarchyBuilder {

    /**
     * Monta a hierarquia a partir das tags parseadas.
     * Utiliza pilha auxiliar para rastrear o nó corrente.
     */
    public HtmlNode construirHierarquia(List<ParsedTag> tags) {
        HtmlNode raiz = null;
        Stack<HtmlNode> pilhaNos = new Stack<>();

        for (ParsedTag tag : tags) {
            if ("?".equals(tag.getNome()) || "!doctype".equals(TagUtils.normalizar(tag.getNome()))) {
                continue;
            }

            switch (tag.getTipo()) {
                case ABERTURA:
                    HtmlNode novoNo = new HtmlNode(TagUtils.normalizar(tag.getNome()), null);
                    if (raiz == null) {
                        raiz = novoNo;
                    } else if (!pilhaNos.isEmpty()) {
                        pilhaNos.peek().adicionarFilho(novoNo);
                    }
                    pilhaNos.push(novoNo);
                    break;

                case AUTOFECHAMENTO:
                    HtmlNode noAutofechamento = new HtmlNode(TagUtils.normalizar(tag.getNome()), null);
                    if (raiz == null) {
                        raiz = noAutofechamento;
                    } else if (!pilhaNos.isEmpty()) {
                        pilhaNos.peek().adicionarFilho(noAutofechamento);
                    }
                    break;

                case FECHAMENTO:
                    if (!pilhaNos.isEmpty()
                            && TagUtils.tagsEquivalentes(pilhaNos.peek().getTag(), tag.getNome())) {
                        pilhaNos.pop();
                    }
                    break;

                default:
                    break;
            }
        }

        return raiz;
    }
}
