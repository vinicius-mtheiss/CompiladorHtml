package br.edu.htmlanalyzer.test;

import br.edu.htmlanalyzer.datastructure.MergeSort;
import br.edu.htmlanalyzer.model.TagStatistics;
import br.edu.htmlanalyzer.model.TagType;

/**
 * Casos de teste para a implementação manual do MergeSort.
 */
public class MergeSortTest {

    private static int falhas = 0;

    public static void main(String[] args) {
        testarOrdenacaoStrings();
        testarOrdenacaoTagStatistics();
        testarArrayVazio();
        testarArrayUnitario();

        System.out.println("MergeSortTest: " + (falhas == 0 ? "TODOS PASSARAM" : falhas + " FALHA(S)"));
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void testarOrdenacaoStrings() {
        String[] array = {"zebra", "alpha", "charlie", "delta"};
        MergeSort.sort(array);
        assertEquals("alpha", array[0], "Primeiro elemento ordenado");
        assertEquals("charlie", array[1], "Segundo elemento");
        assertEquals("delta", array[2], "Terceiro elemento");
        assertEquals("zebra", array[3], "Quarto elemento");
    }

    private static void testarOrdenacaoTagStatistics() {
        TagStatistics[] array = {
                new TagStatistics("div", 2, TagType.ABERTURA, 1),
                new TagStatistics("body", 1, TagType.ABERTURA, 2),
                new TagStatistics("html", 1, TagType.ABERTURA, 1)
        };
        MergeSort.sort(array);
        assertEquals("body", array[0].getTag(), "body primeiro");
        assertEquals("div", array[1].getTag(), "div segundo");
        assertEquals("html", array[2].getTag(), "html terceiro");
    }

    private static void testarArrayVazio() {
        String[] array = {};
        MergeSort.sort(array);
        assertEquals(0, array.length, "Array vazio permanece vazio");
    }

    private static void testarArrayUnitario() {
        Integer[] array = {42};
        MergeSort.sort(array);
        assertEquals(42, array[0], "Array unitário inalterado");
    }

    private static void assertEquals(Object esperado, Object obtido, String mensagem) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhas++;
            System.err.println("FALHA: " + mensagem + " | Esperado: " + esperado + " | Obtido: " + obtido);
        }
    }
}
