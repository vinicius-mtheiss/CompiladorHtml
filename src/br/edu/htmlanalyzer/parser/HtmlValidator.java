package br.edu.htmlanalyzer.parser;

import br.edu.htmlanalyzer.datastructure.Stack;
import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.ErrorType;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.List;

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
     * Interrompe apenas em fechamentos inválidos; tags não finalizadas
     * são listadas todas antes de continuar ou ao final do arquivo.
     */
    public List<AnalysisError> validar(List<ParsedTag> tags) {
        List<AnalysisError> erros = new java.util.ArrayList<>();
        Stack<TagAberta> pilha = new Stack<>();

        for (ParsedTag tag : tags) {
            if ("?".equals(tag.getNome())) {
                erros.add(new AnalysisError(
                        ErrorType.TAG_MALFORMADA,
                        "Foi encontrada uma tag malformada.",
                        tag.getLinha(),
                        tag.getOriginal()
                ));
                return erros;
            }

            switch (tag.getTipo()) {
                case ABERTURA:
                    pilha.push(new TagAberta(tag.getNome(), tag.getLinha()));
                    break;

                case AUTOFECHAMENTO:
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
                        return erros;
                    }

                    TagAberta aberta = pilha.peek();
                    if (TagUtils.tagsEquivalentes(aberta.nome, tag.getNome())) {
                        pilha.pop();
                    } else if (tagEstaAbertaNaPilha(pilha, tag.getNome())) {
                        while (!pilha.isEmpty()
                                && !TagUtils.tagsEquivalentes(pilha.peek().nome, tag.getNome())) {
                            erros.add(criarErroTagNaoFinalizada(pilha.peek()));
                            pilha.pop();
                        }
                        if (!pilha.isEmpty()) {
                            pilha.pop();
                        }
                    } else {
                        erros.add(new AnalysisError(
                                ErrorType.TAG_FINAL_INESPERADA,
                                String.format("Foi encontrada a tag final </%s>, mas era esperada a tag final </%s>.",
                                        tag.getNome(), aberta.nome),
                                tag.getLinha(),
                                tag.getNome()
                        ));
                        return erros;
                    }
                    break;

                default:
                    break;
            }
        }

        while (!pilha.isEmpty()) {
            erros.add(criarErroTagNaoFinalizada(pilha.peek()));
            pilha.pop();
        }

        return erros;
    }

    private AnalysisError criarErroTagNaoFinalizada(TagAberta aberta) {
        return new AnalysisError(
                ErrorType.TAGS_NAO_FINALIZADAS,
                String.format("Falta a tag final </%s> após a linha %d.",
                        aberta.nome, aberta.linha),
                aberta.linha,
                "</" + aberta.nome + ">"
        );
    }

    private boolean tagEstaAbertaNaPilha(Stack<TagAberta> pilha, String nome) {
        for (TagAberta aberta : pilha) {
            if (TagUtils.tagsEquivalentes(aberta.nome, nome)) {
                return true;
            }
        }
        return false;
    }
}
