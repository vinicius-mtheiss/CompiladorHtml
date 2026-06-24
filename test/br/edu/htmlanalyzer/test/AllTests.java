package br.edu.htmlanalyzer.test;

/**
 * Executa todos os casos de teste do projeto.
 */
public class AllTests {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Executando todos os testes ===\n");
        StackTest.main(args);
        QueueTest.main(args);
        MergeSortTest.main(args);
        HtmlValidatorTest.main(args);
        IntegrationTest.main(args);
        System.out.println("\n=== Todos os testes concluídos com sucesso ===");
    }
}
