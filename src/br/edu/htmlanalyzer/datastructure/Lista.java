package br.edu.htmlanalyzer.datastructure;

/**
 * SUMÁRIO DO ARQUIVO: implementa uma lista genérica encadeada, sem usar
 * nenhuma estrutura de dados pronta do Java.
 * POR QUE ESTÁ SEPARADO: várias partes do projeto precisam guardar sequências;
 * centralizar isso evita repetir a mesma estrutura manual em cada classe.
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista simplesmente encadeada criada manualmente para armazenar elementos em
 * ordem de inserção.
 *
 * @param <T> tipo dos elementos armazenados
 */
public class Lista<T> implements Iterable<T> {

    // Nó interno que guarda um elemento e a ligação para o próximo nó da lista.
    private static class No<T> {

        // Valor armazenado nesta posição lógica da lista.
        private T valor;
        // Próxima posição encadeada; null indica fim da lista.
        private No<T> proximo;

        // Cria um nó novo com o valor recebido.
        No(T valor) {
            this.valor = valor;
        }
    }

    // Primeiro nó da lista, usado para iniciar leituras e buscas por índice.
    private No<T> primeiro;
    // Último nó da lista, usado para inserir no fim sem percorrer tudo.
    private No<T> ultimo;
    // Quantidade de elementos armazenados no momento.
    private int quantidade;

    // Cria uma lista inicialmente vazia.
    public Lista() {
        this.primeiro = null;
        this.ultimo = null;
        this.quantidade = 0;
    }

    // Cria uma cópia independente de outra lista manual.
    public Lista(Lista<T> origem) {
        this();
        if (origem != null) {
            for (T elemento : origem) {
                add(elemento);
            }
        }
    }

    // Adiciona um elemento ao final, preservando a ordem de chegada.
    public void add(T elemento) {
        No<T> novo = new No<>(elemento);
        if (isEmpty()) {
            primeiro = novo;
            ultimo = novo;
        } else {
            ultimo.proximo = novo;
            ultimo = novo;
        }
        quantidade++;
    }

    // Substitui o elemento de uma posição existente.
    public void set(int indice, T elemento) {
        noNaPosicao(indice).valor = elemento;
    }

    // Consulta o elemento de uma posição, contando a partir de zero.
    public T get(int indice) {
        return noNaPosicao(indice).valor;
    }

    // Remove todos os elementos, liberando as ligações entre nós.
    public void clear() {
        primeiro = null;
        ultimo = null;
        quantidade = 0;
    }

    // Informa se a lista não possui nenhum elemento.
    public boolean isEmpty() {
        return quantidade == 0;
    }

    // Devolve a quantidade de itens armazenados.
    public int size() {
        return quantidade;
    }

    // Localiza o nó de uma posição válida.
    private No<T> noNaPosicao(int indice) {
        if (indice < 0 || indice >= quantidade) {
            throw new IndexOutOfBoundsException("Indice fora da lista: " + indice);
        }
        No<T> atual = primeiro;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            // Nó que será entregue na próxima chamada de next.
            private No<T> atual = primeiro;

            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T valor = atual.valor;
                atual = atual.proximo;
                return valor;
            }
        };
    }
}
