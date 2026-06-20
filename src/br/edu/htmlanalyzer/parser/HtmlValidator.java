package br.edu.htmlanalyzer.parser;

import br.edu.htmlanalyzer.datastructure.Stack;
import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.ErrorType;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.ArrayList;
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
     */
    public List<AnalysisError> validar(List<ParsedTag> tags) {
        List<AnalysisError> erros = new ArrayList<>();
        Stack<TagAberta> pilha = new Stack<>();

        for (ParsedTag tag : tags) {
            if ("?".equals(tag.getNome())) {
                erros.add(new AnalysisError(
                        ErrorType.TAG_MALFORMADA,
                        "Tag malformada detectada: " + tag.getOriginal(),
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
                                "Tag de fechamento sem tag de abertura correspondente.",
                                tag.getLinha(),
                                tag.getNome()
                        ));
                    } else {
                        TagAberta aberta = pilha.pop();
                        if (!TagUtils.tagsEquivalentes(aberta.nome, tag.getNome())) {
                            erros.add(new AnalysisError(
                                    ErrorType.TAG_FINAL_INESPERADA,
                                    String.format("Esperava fechamento de <%s>, mas encontrou <%s>.",
                                            aberta.nome, tag.getNome()),
                                    tag.getLinha(),
                                    tag.getNome()
                            ));
                            // Reempilha a tag aberta para continuar a verificação.
                            pilha.push(aberta);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        while (!pilha.isEmpty()) {
            TagAberta tagAberta = pilha.pop();
            erros.add(new AnalysisError(
                    ErrorType.TAGS_NAO_FINALIZADAS,
                    "Tag de abertura sem fechamento correspondente.",
                    tagAberta.linha,
                    tagAberta.nome
            ));
        }

        return erros;
    }
}
