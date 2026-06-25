package br.edu.htmlanalyzer.service;

/**
 * SUMÁRIO DO ARQUIVO: transforma a sequência válida de tags em uma árvore de
 * HtmlNode, mantendo o elemento atualmente aberto em uma pilha auxiliar.
 * POR QUE ESTÁ SEPARADO: construir a estrutura visual da árvore é diferente
 * de validar e de exibir; isso deixa cada etapa pequena e testável.
 */

import br.edu.htmlanalyzer.datastructure.Lista;
import br.edu.htmlanalyzer.datastructure.Stack;
import br.edu.htmlanalyzer.model.HtmlNode;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

/**
 * Constrói a árvore hierárquica do documento HTML quando este é válido.
 */
public class HierarchyBuilder {

    // Nome de um nó artificial usado quando o arquivo possui mais de um elemento de nível superior.
    private static final String RAIZ_DOCUMENTO = "#document";

    /**
     * Monta a hierarquia a partir das tags parseadas.
     * Utiliza pilha auxiliar para rastrear o nó corrente.
     */
    public HtmlNode construirHierarquia(Lista<ParsedTag> tags) {
        // A raiz ainda é desconhecida antes de visitar a primeira tag utilizável.
        HtmlNode raiz = null;
        // Pilha acompanha o nó aberto que receberá os próximos filhos.
        Stack<HtmlNode> pilhaNos = new Stack<>();

        // Percorre as tags na ordem em que aparecem para reconstruir o aninhamento original.
        for (ParsedTag tag : tags) {
            // Ignora marcadores de erro e DOCTYPE, pois nenhum deles representa nó visual da hierarquia.
            if ("?".equals(tag.getNome()) || "!doctype".equals(TagUtils.normalizar(tag.getNome()))) {
                continue;
            }

            // Escolhe a operação de árvore que corresponde ao papel da tag.
            switch (tag.getTipo()) {
                case ABERTURA:
                    // O pai é o último nó aberto; sem ele, o novo nó está no nível superior.
                    HtmlNode pai = pilhaNos.isEmpty() ? null : pilhaNos.peek();
                    // Cria o nó usando a forma normalizada do nome para uma visualização consistente.
                    HtmlNode novoNo = new HtmlNode(TagUtils.normalizar(tag.getNome()), pai);
                    // Conecta o nó à raiz ou ao pai atual e atualiza a referência de raiz se necessário.
                    raiz = anexarNo(raiz, pilhaNos, novoNo);
                    // Mantém essa abertura como pai das próximas tags internas.
                    pilhaNos.push(novoNo);
                    break;

                case AUTOFECHAMENTO:
                    // Singleton também pertence ao nó aberto atual, mas ele próprio nunca fica aberto.
                    HtmlNode paiDoSingleton = pilhaNos.isEmpty() ? null : pilhaNos.peek();
                    // Cria o nó folha do singleton.
                    HtmlNode noAutofechamento = new HtmlNode(
                            TagUtils.normalizar(tag.getNome()), paiDoSingleton);
                    // Conecta a folha sem empilhá-la, porque ela não terá filhos.
                    raiz = anexarNo(raiz, pilhaNos, noAutofechamento);
                    break;

                case FECHAMENTO:
                    // Em documento válido, a tag final correspondente encerra o nó do topo.
                    if (!pilhaNos.isEmpty()
                            && TagUtils.tagsEquivalentes(pilhaNos.peek().getTag(), tag.getNome())) {
                        // Remove o nó fechado para que seu pai volte a ser o contexto atual.
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
        // O primeiro nó real observado se torna a raiz provisória.
        if (raiz == null) {
            return novoNo;
        }
        // Com uma tag aberta, o novo nó é filho direto do topo da pilha.
        if (!pilhaNos.isEmpty()) {
            pilhaNos.peek().adicionarFilho(novoNo);
            return raiz;
        }
        // Outra raiz real exige um contêiner virtual que represente o documento inteiro.
        HtmlNode documento = garantirRaizDocumento(raiz);
        // Insere o novo elemento de nível superior dentro do contêiner.
        documento.adicionarFilho(novoNo);
        return documento;
    }

    private HtmlNode garantirRaizDocumento(HtmlNode raiz) {
        // Se a raiz já é o agrupador virtual, apenas a reutiliza.
        if (RAIZ_DOCUMENTO.equals(raiz.getTag())) {
            return raiz;
        }
        // Cria o agrupador sem pai, pois ele representa o documento todo.
        HtmlNode documento = new HtmlNode(RAIZ_DOCUMENTO, null);
        // Preserva a antiga raiz como primeiro filho do novo agrupador.
        documento.adicionarFilho(raiz);
        return documento;
    }
}
