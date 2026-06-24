package br.edu.htmlanalyzer.datastructure;

/**
 * SUMÁRIO DO ARQUIVO: oferece o algoritmo MergeSort para ordenar arrays de
 * objetos comparáveis em ordem crescente, sem depender da ordenação pronta.
 * POR QUE ESTÁ SEPARADO: ordenação é uma responsabilidade genérica, usada
 * pelas estatísticas sem acoplar o serviço à implementação do algoritmo.
 */

/**
 * Implementação manual do algoritmo MergeSort para ordenação estável.
 */
public final class MergeSort {

    // Construtor privado impede criar instâncias, pois todos os recursos da classe são estáticos.
    private MergeSort() {
        // Classe utilitária.
    }

    /**
     * Ordena o array utilizando MergeSort (ordenação crescente).
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        // Arrays nulos, vazios ou unitários já estão ordenados e não exigem trabalho.
        if (array == null || array.length <= 1) {
            return;
        }
        // Inicia a divisão recursiva cobrindo o primeiro e o último índice do array.
        mergeSort(array, 0, array.length - 1);
    }

    // Divide recursivamente o intervalo informado até cada parte ter no máximo um elemento.
    private static <T extends Comparable<T>> void mergeSort(T[] array, int inicio, int fim) {
        // Um intervalo vazio ou de um único item já respeita a ordem.
        if (inicio >= fim) {
            return;
        }
        // Calcula o meio sem risco de somar índices grandes diretamente.
        int meio = inicio + (fim - inicio) / 2;
        // Ordena a metade esquerda.
        mergeSort(array, inicio, meio);
        // Ordena a metade direita.
        mergeSort(array, meio + 1, fim);
        // Une as duas metades ordenadas em um único trecho ordenado.
        merge(array, inicio, meio, fim);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void merge(T[] array, int inicio, int meio, int fim) {
        // Calcula quantos elementos pertencem à metade esquerda, incluindo o meio.
        int tamanhoEsquerda = meio - inicio + 1;
        // Calcula quantos elementos restam na metade direita.
        int tamanhoDireita = fim - meio;

        // Cria um apoio temporário para cada metade; Comparable permite acomodar qualquer T comparável.
        T[] esquerda = (T[]) new Comparable[tamanhoEsquerda];
        T[] direita = (T[]) new Comparable[tamanhoDireita];

        // Copia a primeira metade do array original para o apoio esquerdo.
        System.arraycopy(array, inicio, esquerda, 0, tamanhoEsquerda);
        // Copia a segunda metade para o apoio direito.
        System.arraycopy(array, meio + 1, direita, 0, tamanhoDireita);

        // Aponta para o próximo item ainda não combinado na esquerda.
        int i = 0;
        // Aponta para o próximo item ainda não combinado na direita.
        int j = 0;
        // Indica onde o próximo menor item deve ser escrito no array original.
        int k = inicio;

        // Compara enquanto ainda existirem candidatos nas duas metades.
        while (i < tamanhoEsquerda && j < tamanhoDireita) {
            // Em empate escolhe a esquerda, preservando a estabilidade da ordenação.
            if (esquerda[i].compareTo(direita[j]) <= 0) {
                // Copia a menor escolha e avança tanto a origem quanto o destino.
                array[k++] = esquerda[i++];
            } else {
                // Faz o mesmo quando o menor item pertence à metade direita.
                array[k++] = direita[j++];
            }
        }

        // Se a direita acabou primeiro, transfere os itens restantes da esquerda.
        while (i < tamanhoEsquerda) {
            array[k++] = esquerda[i++];
        }

        // Se a esquerda acabou primeiro, transfere os itens restantes da direita.
        while (j < tamanhoDireita) {
            array[k++] = direita[j++];
        }
    }
}
