package br.edu.htmlanalyzer.parser;

/**
 * SUMÁRIO DO ARQUIVO: valida se as tags já extraídas abrem e fecham em uma
 * ordem estruturalmente correta, acumulando os problemas encontrados.
 * POR QUE ESTÁ SEPARADO: interpretar texto HTML e decidir se sua sequência é
 * válida são tarefas diferentes; esta classe recebe objetos ParsedTag prontos.
 */

import br.edu.htmlanalyzer.datastructure.Lista;
import br.edu.htmlanalyzer.datastructure.Queue;
import br.edu.htmlanalyzer.datastructure.Stack;
import br.edu.htmlanalyzer.model.AnalysisError;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

/**
 * Valida o balanceamento estrutural de tags HTML utilizando uma Pilha.
 * Os erros detectados são armazenados em uma Fila (FIFO) para exibição ordenada.
 * A validação percorre todo o documento, acumulando todos os erros encontrados.
 */
public class HtmlValidator {

    // Tipo interno que associa uma tag aberta à linha onde ela começou.
    private static class TagAberta {

        // Nome da tag aguardando fechamento.
        private final String nome;
        // Linha da abertura, preservada para futuras mensagens ou evolução da validação.
        private final int linha;

        // Cria o registro que será empilhado para uma tag de abertura.
        TagAberta(String nome, int linha) {
            this.nome = nome;
            this.linha = linha;
        }
    }

    /**
     * Valida a sequência de tags e retorna todos os erros encontrados.
     * Tags malformadas ou fechamentos inválidos são registrados e a análise continua.
     */
    public Lista<AnalysisError> validar(Lista<ParsedTag> tags) {
        // Fila conserva a ordem em que os erros foram detectados para o relatório.
        Queue<AnalysisError> erros = new Queue<>();
        // Pilha mantém a última tag aberta, que deve ser a próxima a fechar.
        Stack<TagAberta> pilha = new Stack<>();

        // Analisa cada tag na ordem em que aparece no arquivo.
        for (ParsedTag tag : tags) {
            // O parser usa "?" como marcador para uma tag cuja sintaxe não pôde ser interpretada.
            if ("?".equals(tag.getNome())) {
                // Registra o problema preservando a linha e o trecho original.
                erros.enqueue(AnalysisError.tagMalformada(tag.getLinha(), tag.getOriginal()));
                // Passa à próxima tag, pois a atual não é estruturalmente utilizável.
                continue;
            }

            // Escolhe a ação de acordo com o papel estrutural já definido pelo parser.
            switch (tag.getTipo()) {
                case ABERTURA:
                    // Uma abertura precisa ser fechada depois, então fica no topo da pilha.
                    pilha.push(new TagAberta(tag.getNome(), tag.getLinha()));
                    break;

                case AUTOFECHAMENTO:
                    // Singleton não cria nível de aninhamento e, portanto, não entra na pilha.
                    break;

                case FECHAMENTO:
                    // Delega a comparação da tag final com as aberturas pendentes.
                    processarFechamento(tag, pilha, erros);
                    break;

                default:
                    // Protege contra valores futuros da enumeração sem alterar o estado atual.
                    break;
            }
        }

        // Ao fim do arquivo, tudo que sobrou na pilha representa abertura sem fechamento.
        while (!pilha.isEmpty()) {
            // Informa qual tag do topo era esperada como tag final.
            erros.enqueue(AnalysisError.tagsNaoFinalizadas(pilha.peek().nome));
            // Remove a tag já registrada para que o laço avance para a próxima pendência.
            pilha.pop();
        }

        // Materializa os erros em lista própria na mesma ordem FIFO em que foram enfileirados.
        return erros.toLista();
    }

    // Trata uma tag de fechamento comparando-a com as aberturas ainda pendentes.
    private void processarFechamento(ParsedTag tag, Stack<TagAberta> pilha, Queue<AnalysisError> erros) {
        // Sem abertura pendente, a tag final não tem par correspondente.
        if (pilha.isEmpty()) {
            erros.enqueue(AnalysisError.tagFinalSemTagInicial(tag.getLinha(), tag.getNome()));
            return;
        }

        // Consulta a última abertura, que é a única candidata a um fechamento corretamente aninhado.
        TagAberta aberta = pilha.peek();
        // Se os nomes equivalem, o fechamento correto consome a abertura do topo.
        if (TagUtils.tagsEquivalentes(aberta.nome, tag.getNome())) {
            pilha.pop();
        // Se a tag aparece mais abaixo, registra as camadas intermediárias como não finalizadas.
        } else if (tagEstaAbertaNaPilha(pilha, tag.getNome())) {
            // Remove somente as aberturas que bloqueiam a tag encontrada.
            while (!pilha.isEmpty()
                    && !TagUtils.tagsEquivalentes(pilha.peek().nome, tag.getNome())) {
                // Cada camada pulada exige uma tag final que não foi escrita antes.
                erros.enqueue(AnalysisError.tagsNaoFinalizadas(pilha.peek().nome));
                pilha.pop();
            }
            // Remove a abertura que enfim corresponde ao fechamento atual.
            if (!pilha.isEmpty()) {
                pilha.pop();
            }
        } else {
            // A tag final não corresponde a nenhuma abertura pendente; informa qual era esperada no topo.
            erros.enqueue(AnalysisError.tagFinalInesperada(
                    tag.getLinha(), tag.getNome(), aberta.nome));
        }
    }

    private boolean tagEstaAbertaNaPilha(Stack<TagAberta> pilha, String nome) {
        // O iterador da pilha percorre do topo para a base, verificando cada abertura pendente.
        for (TagAberta aberta : pilha) {
            // Uma comparação normalizada trata maiúsculas e minúsculas como equivalentes.
            if (TagUtils.tagsEquivalentes(aberta.nome, nome)) {
                // Encontrar a tag prova que o fechamento existe, embora possa estar fora da ordem ideal.
                return true;
            }
        }
        // Nenhuma abertura compatível foi encontrada.
        return false;
    }
}
