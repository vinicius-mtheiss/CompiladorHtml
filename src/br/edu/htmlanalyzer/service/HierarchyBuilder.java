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

    private static final String RAIZ_DOCUMENTO = "#document";

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
                    HtmlNode pai = pilhaNos.isEmpty() ? null : pilhaNos.peek();
                    HtmlNode novoNo = new HtmlNode(TagUtils.normalizar(tag.getNome()), pai);
                    raiz = anexarNo(raiz, pilhaNos, novoNo);
                    pilhaNos.push(novoNo);
                    break;

                case AUTOFECHAMENTO:
                    HtmlNode paiDoSingleton = pilhaNos.isEmpty() ? null : pilhaNos.peek();
                    HtmlNode noAutofechamento = new HtmlNode(
                            TagUtils.normalizar(tag.getNome()), paiDoSingleton);
                    raiz = anexarNo(raiz, pilhaNos, noAutofechamento);
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

    /**
     * Insere um nó na árvore. Quando a pilha está vazia e já existe raiz,
     * cria um nó virtual para agrupar múltiplos elementos no nível do documento.
     */
    private HtmlNode anexarNo(HtmlNode raiz, Stack<HtmlNode> pilhaNos, HtmlNode novoNo) {
        if (raiz == null) {
            return novoNo;
        }
        if (!pilhaNos.isEmpty()) {
            pilhaNos.peek().adicionarFilho(novoNo);
            return raiz;
        }
        HtmlNode documento = garantirRaizDocumento(raiz);
        documento.adicionarFilho(novoNo);
        return documento;
    }

    private HtmlNode garantirRaizDocumento(HtmlNode raiz) {
        if (RAIZ_DOCUMENTO.equals(raiz.getTag())) {
            return raiz;
        }
        HtmlNode documento = new HtmlNode(RAIZ_DOCUMENTO, null);
        documento.adicionarFilho(raiz);
        return documento;
    }
}
