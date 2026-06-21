package br.edu.htmlanalyzer.parser;

import br.edu.htmlanalyzer.datastructure.Stack;
import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.ErrorType;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * Valida o balanceamento estrutural de tags HTML utilizando uma Pilha.
 */
public class HtmlValidator {

    private static class TagAberta {

        private final String nome;
        private final int linha;

        TagAberta(String nome, int linha) {
            this.nome = nome;
            this.linha = linha;
        }
    }

    /**
     * Valida a sequência de tags e retorna a lista de erros encontrados.
     */
    public List<AnalysisError> validar(List<ParsedTag> tags) {
        Queue<AnalysisError> erros = new ArrayDeque<>();
        Stack<TagAberta> pilha = new Stack<>();

        for (ParsedTag tag : tags) {
            if ("?".equals(tag.getNome())) {
                erros.add(new AnalysisError(
                        ErrorType.TAG_MALFORMADA,
                        "Foi encontrada uma tag malformada.",
                        tag.getLinha(),
                        tag.getOriginal()
                ));
                continue;
            }

            switch (tag.getTipo()) {
                case ABERTURA:
                    pilha.push(new TagAberta(tag.getNome(), tag.getLinha()));
                    break;

                case AUTOFECHAMENTO:
                    // Tags singleton não são empilhadas.
                    break;

                case FECHAMENTO:
                    if (pilha.isEmpty()) {
                        erros.add(new AnalysisError(
                                ErrorType.TAG_FINAL_SEM_ABERTURA,
                                String.format("Foi encontrada a tag final </%s>, mas não existe tag inicial correspondente.",
                                        tag.getNome()),
                                tag.getLinha(),
                                tag.getNome()
                        ));
                    } else {
                        TagAberta aberta = pilha.peek();
                        if (!TagUtils.tagsEquivalentes(aberta.nome, tag.getNome())) {
                            erros.add(new AnalysisError(
                                    ErrorType.TAG_FINAL_INESPERADA,
                                    String.format("Foi encontrada a tag final </%s>, mas era esperada a tag final </%s>.",
                                            tag.getNome(), aberta.nome),
                                    tag.getLinha(),
                                    tag.getNome()
                            ));
                        } else {
                            pilha.pop();
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        if (!pilha.isEmpty()) {
            StringBuilder esperadas = new StringBuilder();
            int primeiraLinha = pilha.peek().linha;
            while (!pilha.isEmpty()) {
                if (esperadas.length() > 0) {
                    esperadas.append(", ");
                }
                esperadas.append("</").append(pilha.pop().nome).append(">");
            }
            erros.add(new AnalysisError(ErrorType.TAGS_NAO_FINALIZADAS,
                    "Faltam tags finais no arquivo. Tags esperadas: " + esperadas + ".",
                    primeiraLinha, esperadas.toString()));
        }

        return new java.util.ArrayList<>(erros);
    }
}
