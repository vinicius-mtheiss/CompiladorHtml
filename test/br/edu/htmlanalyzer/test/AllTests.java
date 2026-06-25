package br.edu.htmlanalyzer.test;

/**
 * SUMÁRIO DO ARQUIVO: oferece um único comando para executar todas as classes
 * de teste main-driven na sequência definida pelo projeto.
 * POR QUE ESTÁ SEPARADO: centraliza a suíte completa, evitando que quem testa
 * precise lembrar manualmente cada classe que deve ser executada.
 */

/**
 * Executa todos os casos de teste do projeto.
 */
public class AllTests {

    // Recebe os argumentos da linha de comando e os repassa a cada teste independente.
    public static void main(String... args) throws Exception {
        // Exibe um cabeçalho para tornar a execução no terminal mais fácil de acompanhar.
        System.out.println("=== Executando todos os testes ===\n");
        // Executa cada conjunto em sequência; uma falha encerra com código 1 dentro da própria classe.
        StackTest.main();
        QueueTest.main();
        MergeSortTest.main();
        HtmlValidatorTest.main();
        IntegrationTest.main();
        // Informa que nenhuma classe interrompeu a suíte por falha.
        System.out.println("\n=== Todos os testes concluídos com sucesso ===");
    }
}
