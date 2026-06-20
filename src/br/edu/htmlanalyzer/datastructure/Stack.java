package br.edu.htmlanalyzer.datastructure;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementação manual de uma Pilha (LIFO) baseada em array dinâmico.
 *
 * @param <T> tipo dos elementos armazenados
 */
public class Stack<T> implements Iterable<T> {

    private static final int CAPACIDADE_INICIAL = 16;

    private Object[] elementos;
    private int topo;

    public Stack() {
        this.elementos = new Object[CAPACIDADE_INICIAL];
        this.topo = -1;
    }

    public void push(T elemento) {
        if (topo == elementos.length - 1) {
            redimensionar();
        }
        elementos[++topo] = elemento;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T removido = (T) elementos[topo];
        elementos[topo--] = null;
        return removido;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return (T) elementos[topo];
    }

    public boolean isEmpty() {
        return topo < 0;
    }

    public int size() {
        return topo + 1;
    }

    /**
     * Retorna uma cópia dos elementos atuais da pilha (do topo para a base).
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] copia = (T[]) new Object[size()];
        for (int i = 0; i <= topo; i++) {
            copia[i] = (T) elementos[i];
        }
        return copia;
    }

    private void redimensionar() {
        Object[] novoArray = new Object[elementos.length * 2];
        System.arraycopy(elementos, 0, novoArray, 0, elementos.length);
        elementos = novoArray;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int indice = topo;

            @Override
            public boolean hasNext() {
                return indice >= 0;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T) elementos[indice--];
            }
        };
    }
}
