package br.edu.htmlanalyzer.datastructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementação manual de uma Fila (FIFO) baseada em array circular dinâmico.
 *
 * @param <T> tipo dos elementos armazenados
 */
public class Queue<T> implements Iterable<T> {

    private static final int CAPACIDADE_INICIAL = 16;

    private Object[] elementos;
    private int inicio;
    private int fim;
    private int quantidade;

    public Queue() {
        this.elementos = new Object[CAPACIDADE_INICIAL];
        this.inicio = 0;
        this.fim = -1;
        this.quantidade = 0;
    }

    public void enqueue(T elemento) {
        if (quantidade == elementos.length) {
            redimensionar();
        }
        fim = (fim + 1) % elementos.length;
        elementos[fim] = elemento;
        quantidade++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Fila vazia.");
        }
        T removido = (T) elementos[inicio];
        elementos[inicio] = null;
        inicio = (inicio + 1) % elementos.length;
        quantidade--;
        return removido;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Fila vazia.");
        }
        return (T) elementos[inicio];
    }

    public boolean isEmpty() {
        return quantidade == 0;
    }

    public int size() {
        return quantidade;
    }

    /**
     * Copia os elementos da fila para uma lista, preservando a ordem FIFO.
     */
    public List<T> toList() {
        List<T> lista = new ArrayList<>(quantidade);
        for (T elemento : this) {
            lista.add(elemento);
        }
        return lista;
    }

    private void redimensionar() {
        Object[] novoArray = new Object[elementos.length * 2];
        for (int i = 0; i < quantidade; i++) {
            novoArray[i] = elementos[(inicio + i) % elementos.length];
        }
        elementos = novoArray;
        inicio = 0;
        fim = quantidade - 1;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int indice = 0;

            @Override
            public boolean hasNext() {
                return indice < quantidade;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T elemento = (T) elementos[(inicio + indice) % elementos.length];
                indice++;
                return elemento;
            }
        };
    }
}
