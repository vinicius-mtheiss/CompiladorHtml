package br.edu.htmlanalyzer.test;

/**
 * SUMÁRIO DO ARQUIVO: testa a fila isoladamente, inclusive sua ordem FIFO e
 * o comportamento depois de aumentar sua capacidade interna.
 * POR QUE ESTÁ SEPARADO: confirma o contrato da Queue sem que uma falha seja
 * confundida com parser, validador ou arquivos HTML de exemplo.
 */

import br.edu.htmlanalyzer.datastructure.Queue;
import br.edu.htmlanalyzer.datastructure.Lista;

/**
 * Casos de teste para a implementação manual da Fila.
 */
public class QueueTest {

    // Acumula o número de verificações que não atenderam ao contrato da fila.
    private static int falhas = 0;

    // Executa todos os cenários de comportamento FIFO.
    public static void main(String... args) {
        testarEnqueueDequeue();
        testarPeek();
        testarVazia();
        testarRedimensionamento();
        testarOrdemFifo();
        testarToList();

        // Mostra no terminal se todos os cenários foram aprovados.
        System.out.println("QueueTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // Confirma entrada e saída na ordem em que os itens chegaram.
    private static void testarEnqueueDequeue() {
        Queue<String> fila = new Queue<>();
        fila.enqueue("A");
        fila.enqueue("B");
        assertEquals("A", fila.dequeue(), "Dequeue deve retornar o primeiro inserido");
        assertEquals("B", fila.dequeue(), "Dequeue subsequente");
        assertTrue(fila.isEmpty(), "Fila deve estar vazia");
    }

    // Confirma que peek não altera o tamanho nem remove o primeiro item.
    private static void testarPeek() {
        Queue<Integer> fila = new Queue<>();
        fila.enqueue(10);
        assertEquals(10, fila.peek(), "Peek não remove elemento");
        assertEquals(1, fila.size(), "Tamanho após peek");
    }

    // Confirma o estado vazio e a exceção ao tentar retirar de uma fila sem itens.
    private static void testarVazia() {
        Queue<String> fila = new Queue<>();
        assertTrue(fila.isEmpty(), "Fila nova deve estar vazia");
        try {
            fila.dequeue();
            falhar("Dequeue em fila vazia deveria lançar exceção");
        } catch (Exception e) {
            // Esperado.
        }
    }

    // Insere muitos itens para verificar se a fila encadeada mantém o controle correto.
    private static void testarRedimensionamento() {
        Queue<Integer> fila = new Queue<>();
        for (int i = 0; i < 100; i++) {
            fila.enqueue(i);
        }
        assertEquals(100, fila.size(), "Fila com 100 elementos");
        assertEquals(0, fila.dequeue(), "Primeiro inserido");
    }

    // Testa explicitamente a regra FIFO com três elementos diferentes.
    private static void testarOrdemFifo() {
        Queue<String> fila = new Queue<>();
        fila.enqueue("1");
        fila.enqueue("2");
        fila.enqueue("3");
        assertEquals("1", fila.dequeue(), "Ordem FIFO - 1");
        assertEquals("2", fila.dequeue(), "Ordem FIFO - 2");
        assertEquals("3", fila.dequeue(), "Ordem FIFO - 3");
    }

    // Garante que a cópia para Lista própria preserva tamanho e ordem da fila.
    private static void testarToList() {
        Queue<String> fila = new Queue<>();
        fila.enqueue("A");
        fila.enqueue("B");
        Lista<String> lista = fila.toLista();
        assertEquals(2, lista.size(), "Lista com 2 elementos");
        assertEquals("A", lista.get(0), "Primeiro elemento da lista");
        assertEquals("B", lista.get(1), "Segundo elemento da lista");
    }

    // Compara resultado esperado e obtido, registrando contexto quando falhar.
    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }

    // Registra falha para uma condição booleana falsa.
    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }

    // Registra uma falha incondicional, usada depois de uma exceção esperada não ocorrer.
    private static void falhar(String mensagem) {
        falhas++;
        System.err.println("FALHA: " + mensagem);
    }
}
