package br.edu.htmlanalyzer.test;

/**
 * SUMÁRIO DO ARQUIVO: verifica a ordenação MergeSort em strings, estatísticas
 * de tags, lista vazia e lista de um único elemento.
 * POR QUE ESTÁ SEPARADO: garante a correção do algoritmo de ordenação antes
 * que ele seja usado pelo serviço de estatísticas do projeto.
 */

import br.edu.htmlanalyzer.datastructure.Lista;
import br.edu.htmlanalyzer.datastructure.MergeSort;
import br.edu.htmlanalyzer.model.TagStatistics;
import br.edu.htmlanalyzer.model.TagType;

/**
 * Casos de teste para a implementação manual do MergeSort.
 */
public class MergeSortTest {

    // Acumula as falhas que a execução encontrou.
    private static int falhas = 0;

    // Executa os cenários que cobrem tipos e tamanhos relevantes de lista.
    public static void main(String... args) {
        testarOrdenacaoStrings();
        testarOrdenacaoTagStatistics();
        testarListaVazia();
        testarListaUnitaria();

        System.out.println("MergeSortTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // Ordena palavras para verificar a comparação natural de String.
    private static void testarOrdenacaoStrings() {
        Lista<String> lista = new Lista<>();
        lista.add("zebra");
        lista.add("alpha");
        lista.add("charlie");
        lista.add("delta");
        MergeSort.sort(lista);
        assertEquals("alpha", lista.get(0), "Primeiro elemento ordenado");
        assertEquals("charlie", lista.get(1), "Segundo elemento");
        assertEquals("delta", lista.get(2), "Terceiro elemento");
        assertEquals("zebra", lista.get(3), "Quarto elemento");
    }

    // Ordena objetos do modelo para confirmar o uso de Comparable em tipos próprios.
    private static void testarOrdenacaoTagStatistics() {
        Lista<TagStatistics> lista = new Lista<>();
        lista.add(new TagStatistics("div", 2, TagType.ABERTURA, 1));
        lista.add(new TagStatistics("body", 1, TagType.ABERTURA, 2));
        lista.add(new TagStatistics("html", 1, TagType.ABERTURA, 1));
        MergeSort.sort(lista);
        assertEquals("body", lista.get(0).getTag(), "body primeiro");
        assertEquals("div", lista.get(1).getTag(), "div segundo");
        assertEquals("html", lista.get(2).getTag(), "html terceiro");
    }

    // Confirma que uma lista vazia é aceita sem erro e permanece vazia.
    private static void testarListaVazia() {
        Lista<String> lista = new Lista<>();
        MergeSort.sort(lista);
        assertEquals(0, lista.size(), "Lista vazia permanece vazia");
    }

    // Confirma que não há alteração indevida em uma lista de apenas um item.
    private static void testarListaUnitaria() {
        Lista<Integer> lista = new Lista<>();
        lista.add(42);
        MergeSort.sort(lista);
        assertEquals(42, lista.get(0), "Lista unitária inalterada");
    }

    // Implementa a asserção mínima do projeto, sem depender de uma biblioteca externa de testes.
    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }
}
