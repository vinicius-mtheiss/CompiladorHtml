package br.edu.htmlanalyzer.test;

/**
 * SUMÁRIO DO ARQUIVO: executa testes isolados da pilha, cobrindo inserção,
 * remoção, consulta, exceções, aumento de capacidade e iteração.
 * POR QUE ESTÁ SEPARADO: testes da estrutura de dados não devem depender do
 * HTML; falhas aqui apontam diretamente para Stack, com diagnóstico simples.
 */

import br.edu.htmlanalyzer.datastructure.Stack;

/**
 * Casos de teste para a implementação manual da Pilha.
 */
public class StackTest {

    // Conta as verificações executadas, útil durante leitura ou depuração do teste.
    private static int testes = 0;
    // Conta quantas verificações não produziram o resultado esperado.
    private static int falhas = 0;

    // Executa todos os cenários independentes da pilha.
    public static void main(String... args) {
        // Verifica cada operação e característica prometida pela classe Stack.
        testarPushPop();
        testarPeek();
        testarVazia();
        testarRedimensionamento();
        testarIterator();

        // Exibe um resumo que indica claramente o estado da classe testada.
        System.out.println("StackTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        // Um código diferente de zero permite que AllTests ou uma ferramenta de build detecte a falha.
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // Confirma que push insere no topo e pop remove em ordem LIFO.
    private static void testarPushPop() {
        Stack<String> pilha = new Stack<>();
        pilha.push("A");
        pilha.push("B");
        assertEquals("B", pilha.pop(), "Pop deve retornar o topo");
        assertEquals("A", pilha.pop(), "Pop subsequente");
        assertTrue(pilha.isEmpty(), "Pilha deve estar vazia");
    }

    // Confirma que peek consulta o topo sem removê-lo.
    private static void testarPeek() {
        Stack<Integer> pilha = new Stack<>();
        pilha.push(10);
        assertEquals(10, pilha.peek(), "Peek não remove elemento");
        assertEquals(1, pilha.size(), "Tamanho após peek");
    }

    // Confirma o estado inicial e a exceção esperada ao remover de pilha vazia.
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

    // Insere muitos itens para verificar se a pilha encadeada mantém a ordem LIFO.
    private static void testarRedimensionamento() {
        Stack<Integer> pilha = new Stack<>();
        for (int i = 0; i < 100; i++) {
            pilha.push(i);
        }
        assertEquals(100, pilha.size(), "Pilha com 100 elementos");
        assertEquals(99, pilha.pop(), "Último inserido");
    }

    // Confirma que o Iterable percorre todos os elementos da estrutura.
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

    // Compara dois valores e registra uma falha detalhada quando são diferentes.
    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        testes++;
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }

    // Registra uma falha quando uma condição que deveria ser verdadeira é falsa.
    private static void assertTrue(boolean condicao, String mensagem) {
        testes++;
        if (!condicao) {
            falhas++;
            System.err.println("FALHA: " + mensagem);
        }
    }

    // Força uma falha para cenários que deveriam ter lançado exceção.
    private static void falhar(String mensagem) {
        testes++;
        falhas++;
        System.err.println("FALHA: " + mensagem);
    }
}
