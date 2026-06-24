package br.edu.htmlanalyzer.test;

import br.edu.htmlanalyzer.datastructure.Queue;

import java.util.List;

/**
 * Casos de teste para a implementação manual da Fila.
 */
public class QueueTest {

    private static int falhas = 0;

    public static void main(String[] args) {
        testarEnqueueDequeue();
        testarPeek();
        testarVazia();
        testarRedimensionamento();
        testarOrdemFifo();
        testarToList();

        System.out.println("QueueTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void testarEnqueueDequeue() {
        Queue<String> fila = new Queue<>();
        fila.enqueue("A");
        fila.enqueue("B");
        assertEquals("A", fila.dequeue(), "Dequeue deve retornar o primeiro inserido");
        assertEquals("B", fila.dequeue(), "Dequeue subsequente");
        assertTrue(fila.isEmpty(), "Fila deve estar vazia");
    }

    private static void testarPeek() {
        Queue<Integer> fila = new Queue<>();
        fila.enqueue(10);
        assertEquals(10, fila.peek(), "Peek não remove elemento");
        assertEquals(1, fila.size(), "Tamanho após peek");
    }

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

    private static void testarRedimensionamento() {
        Queue<Integer> fila = new Queue<>();
        for (int i = 0; i < 100; i++) {
            fila.enqueue(i);
        }
        assertEquals(100, fila.size(), "Fila com 100 elementos");
        assertEquals(0, fila.dequeue(), "Primeiro inserido");
    }

    private static void testarOrdemFifo() {
        Queue<String> fila = new Queue<>();
        fila.enqueue("1");
        fila.enqueue("2");
        fila.enqueue("3");
        assertEquals("1", fila.dequeue(), "Ordem FIFO - 1");
        assertEquals("2", fila.dequeue(), "Ordem FIFO - 2");
        assertEquals("3", fila.dequeue(), "Ordem FIFO - 3");
    }

    private static void testarToList() {
        Queue<String> fila = new Queue<>();
        fila.enqueue("A");
        fila.enqueue("B");
        List<String> lista = fila.toList();
        assertEquals(2, lista.size(), "Lista com 2 elementos");
        assertEquals("A", lista.get(0), "Primeiro elemento da lista");
        assertEquals("B", lista.get(1), "Segundo elemento da lista");
    }

    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }

    private static void falhar(String mensagem) {
        falhas++;
        System.err.println("FALHA: " + mensagem);
    }
}
