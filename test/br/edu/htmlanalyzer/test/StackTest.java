package br.edu.htmlanalyzer.test;

import br.edu.htmlanalyzer.datastructure.Stack;

/**
 * Casos de teste para a implementação manual da Pilha.
 */
public class StackTest {

    private static int testes = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        testarPushPop();
        testarPeek();
        testarVazia();
        testarRedimensionamento();
        testarIterator();

        System.out.println("StackTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void testarPushPop() {
        Stack<String> pilha = new Stack<>();
        pilha.push("A");
        pilha.push("B");
        assertEquals("B", pilha.pop(), "Pop deve retornar o topo");
        assertEquals("A", pilha.pop(), "Pop subsequente");
        assertTrue(pilha.isEmpty(), "Pilha deve estar vazia");
    }

    private static void testarPeek() {
        Stack<Integer> pilha = new Stack<>();
        pilha.push(10);
        assertEquals(10, pilha.peek(), "Peek não remove elemento");
        assertEquals(1, pilha.size(), "Tamanho após peek");
    }

    private static void testarVazia() {
        Stack<String> pilha = new Stack<>();
        assertTrue(pilha.isEmpty(), "Pilha nova deve estar vazia");
        try {
            pilha.pop();
            falhar("Pop em pilha vazia deveria lançar exceção");
        } catch (Exception e) {
            // Esperado.
        }
    }

    private static void testarRedimensionamento() {
        Stack<Integer> pilha = new Stack<>();
        for (int i = 0; i < 100; i++) {
            pilha.push(i);
        }
        assertEquals(100, pilha.size(), "Pilha com 100 elementos");
        assertEquals(99, pilha.pop(), "Último inserido");
    }

    private static void testarIterator() {
        Stack<String> pilha = new Stack<>();
        pilha.push("X");
        pilha.push("Y");
        int count = 0;
        for (String s : pilha) {
            count++;
            assertTrue(s.equals("X") || s.equals("Y"), "Iteração válida");
        }
        assertEquals(2, count, "Iterator percorre todos os elementos");
    }

    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        testes++;
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        testes++;
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }

    private static void falhar(String mensagem) {
        testes++;
        falhas++;
        System.err.println("FALHA: " + mensagem);
    }
}
