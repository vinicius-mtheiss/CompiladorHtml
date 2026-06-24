package br.edu.htmlanalyzer.parser;

import br.edu.htmlanalyzer.datastructure.Queue;
import br.edu.htmlanalyzer.datastructure.Stack;
import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.List;

/**
 * Valida o balanceamento estrutural de tags HTML utilizando uma Pilha.
 * Os erros detectados são armazenados em uma Fila (FIFO) para exibição ordenada.
 * A validação percorre todo o documento, acumulando todos os erros encontrados.
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
     * Valida a sequência de tags e retorna todos os erros encontrados.
     * Tags malformadas ou fechamentos inválidos são registrados e a análise continua.
     */
    public List<AnalysisError> validar(List<ParsedTag> tags) {
        Queue<AnalysisError> erros = new Queue<>();
        Stack<TagAberta> pilha = new Stack<>();

        for (ParsedTag tag : tags) {
            if ("?".equals(tag.getNome())) {
                erros.enqueue(AnalysisError.tagMalformada(tag.getLinha(), tag.getOriginal()));
                continue;
            }

            switch (tag.getTipo()) {
                case ABERTURA:
                    pilha.push(new TagAberta(tag.getNome(), tag.getLinha()));
                    break;

                case AUTOFECHAMENTO:
                    break;

                case FECHAMENTO:
                    processarFechamento(tag, pilha, erros);
                    break;

                default:
                    break;
            }
        }

        while (!pilha.isEmpty()) {
            erros.enqueue(AnalysisError.tagsNaoFinalizadas(pilha.peek().nome));
            pilha.pop();
        }

        return erros.toList();
    }

    private void processarFechamento(ParsedTag tag, Stack<TagAberta> pilha, Queue<AnalysisError> erros) {
        if (pilha.isEmpty()) {
            erros.enqueue(AnalysisError.tagFinalSemTagInicial(tag.getLinha(), tag.getNome()));
            return;
        }

        TagAberta aberta = pilha.peek();
        if (TagUtils.tagsEquivalentes(aberta.nome, tag.getNome())) {
            pilha.pop();
        } else if (tagEstaAbertaNaPilha(pilha, tag.getNome())) {
            while (!pilha.isEmpty()
                    && !TagUtils.tagsEquivalentes(pilha.peek().nome, tag.getNome())) {
                erros.enqueue(AnalysisError.tagsNaoFinalizadas(pilha.peek().nome));
                pilha.pop();
            }
            if (!pilha.isEmpty()) {
                pilha.pop();
            }
        } else {
            erros.enqueue(AnalysisError.tagFinalInesperada(
                    tag.getLinha(), tag.getNome(), aberta.nome));
        }
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
