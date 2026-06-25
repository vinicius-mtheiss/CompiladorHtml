package br.edu.htmlanalyzer.datastructure;

/**
 * SUMÁRIO DO ARQUIVO: oferece o algoritmo MergeSort para ordenar listas
 * próprias de objetos comparáveis em ordem crescente, sem arrays nem coleções
 * prontas do Java.
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
     * Ordena a lista utilizando MergeSort (ordenação crescente).
     */
    public static <T extends Comparable<T>> void sort(Lista<T> lista) {
        // Listas nulas, vazias ou unitárias já estão ordenadas e não exigem trabalho.
        if (lista == null || lista.size() <= 1) {
            return;
        }
        // Calcula a lista ordenada por divisão e intercalação.
        Lista<T> ordenada = mergeSort(lista);
        // Substitui o conteúdo original pelo resultado ordenado.
        lista.clear();
        for (T elemento : ordenada) {
            lista.add(elemento);
        }
    }

    // Divide recursivamente a lista até cada parte ter no máximo um elemento.
    private static <T extends Comparable<T>> Lista<T> mergeSort(Lista<T> lista) {
        if (lista.size() <= 1) {
            return new Lista<>(lista);
        }

        Lista<T> esquerda = new Lista<>();
        Lista<T> direita = new Lista<>();
        int meio = lista.size() / 2;
        int indice = 0;

        for (T elemento : lista) {
            if (indice < meio) {
                esquerda.add(elemento);
            } else {
                direita.add(elemento);
            }
            indice++;
        }

        return merge(mergeSort(esquerda), mergeSort(direita));
    }

    // Intercala duas listas já ordenadas preservando a estabilidade.
    private static <T extends Comparable<T>> Lista<T> merge(Lista<T> esquerda, Lista<T> direita) {
        Lista<T> resultado = new Lista<>();
        int indiceEsquerda = 0;
        int indiceDireita = 0;

        while (indiceEsquerda < esquerda.size() && indiceDireita < direita.size()) {
            T valorEsquerda = esquerda.get(indiceEsquerda);
            T valorDireita = direita.get(indiceDireita);
            if (valorEsquerda.compareTo(valorDireita) <= 0) {
                resultado.add(valorEsquerda);
                indiceEsquerda++;
            } else {
                resultado.add(valorDireita);
                indiceDireita++;
            }
        }

        while (indiceEsquerda < esquerda.size()) {
            resultado.add(esquerda.get(indiceEsquerda));
            indiceEsquerda++;
        }

        while (indiceDireita < direita.size()) {
            resultado.add(direita.get(indiceDireita));
            indiceDireita++;
        }

        return resultado;
    }
}
