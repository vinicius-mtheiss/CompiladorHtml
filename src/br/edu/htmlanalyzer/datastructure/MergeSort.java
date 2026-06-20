package br.edu.htmlanalyzer.datastructure;

/**
 * Implementação manual do algoritmo MergeSort para ordenação estável.
 */
public final class MergeSort {

    private MergeSort() {
        // Classe utilitária.
    }

    /**
     * Ordena o array utilizando MergeSort (ordenação crescente).
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        mergeSort(array, 0, array.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] array, int inicio, int fim) {
        if (inicio >= fim) {
            return;
        }
        int meio = inicio + (fim - inicio) / 2;
        mergeSort(array, inicio, meio);
        mergeSort(array, meio + 1, fim);
        merge(array, inicio, meio, fim);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void merge(T[] array, int inicio, int meio, int fim) {
        int tamanhoEsquerda = meio - inicio + 1;
        int tamanhoDireita = fim - meio;

        T[] esquerda = (T[]) new Comparable[tamanhoEsquerda];
        T[] direita = (T[]) new Comparable[tamanhoDireita];

        System.arraycopy(array, inicio, esquerda, 0, tamanhoEsquerda);
        System.arraycopy(array, meio + 1, direita, 0, tamanhoDireita);

        int i = 0;
        int j = 0;
        int k = inicio;

        while (i < tamanhoEsquerda && j < tamanhoDireita) {
            if (esquerda[i].compareTo(direita[j]) <= 0) {
                array[k++] = esquerda[i++];
            } else {
                array[k++] = direita[j++];
            }
        }

        while (i < tamanhoEsquerda) {
            array[k++] = esquerda[i++];
        }

        while (j < tamanhoDireita) {
            array[k++] = direita[j++];
        }
    }
}
