package br.edu.htmlanalyzer.datastructure;

/**
 * SUMÁRIO DO ARQUIVO: implementa uma fila genérica, que devolve primeiro o
 * elemento que entrou primeiro (regra FIFO), usando nós encadeados.
 * POR QUE ESTÁ SEPARADO: o validador só precisa registrar erros em ordem;
 * os detalhes de armazenar a fila ficam encapsulados aqui.
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementação manual de uma Fila (FIFO) baseada em nós encadeados.
 *
 * @param <T> tipo dos elementos armazenados
 */
public class Queue<T> implements Iterable<T> {

    // Nó interno que guarda um item e a ligação com o próximo item da fila.
    private static class No<T> {

        // Valor guardado nesta posição da fila.
        private final T valor;
        // Próximo nó na ordem FIFO.
        private No<T> proximo;

        // Cria um nó isolado para receber ligação depois.
        No(T valor) {
            this.valor = valor;
        }
    }

    // Indica o próximo elemento que sairá da fila.
    private No<T> inicio;
    // Indica o último elemento que entrou na fila.
    private No<T> fim;
    // Guarda a quantidade real de itens.
    private int quantidade;

    // Inicializa uma fila vazia e seus ponteiros de controle.
    public Queue() {
        this.inicio = null;
        this.fim = null;
        this.quantidade = 0;
    }

    // Acrescenta um elemento no fim, preservando a ordem FIFO.
    public void enqueue(T elemento) {
        No<T> novo = new No<>(elemento);
        if (isEmpty()) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }
        quantidade++;
    }

    public T dequeue() {
        // Remover de uma fila vazia não tem resultado válido.
        if (isEmpty()) {
            // Explica a causa da falha ao código que chamou a operação.
            throw new NoSuchElementException("Fila vazia.");
        }
        // Lê o elemento mais antigo, localizado no início.
        T removido = inicio.valor;
        // Move o início para o próximo elemento.
        inicio = inicio.proximo;
        // Se a fila ficou vazia, também limpa a referência do fim.
        if (inicio == null) {
            fim = null;
        }
        // Diminui a quantidade real de elementos presentes.
        quantidade--;
        // Devolve o primeiro item que havia entrado na fila.
        return removido;
    }

    public T peek() {
        // Não existe primeiro elemento quando a fila está vazia.
        if (isEmpty()) {
            throw new NoSuchElementException("Fila vazia.");
        }
        // Consulta o primeiro item sem removê-lo nem mover os ponteiros.
        return inicio.valor;
    }

    // Informa se a quantidade controlada chegou a zero.
    public boolean isEmpty() {
        return quantidade == 0;
    }

    // Fornece a quantidade de elementos sem precisar percorrer a fila.
    public int size() {
        return quantidade;
    }

    /**
     * Copia os elementos da fila para uma lista manual, preservando a ordem FIFO.
     */
    public Lista<T> toLista() {
        // Cria uma lista própria para não depender de coleções prontas do Java.
        Lista<T> lista = new Lista<>();
        // Usa o iterador para copiar os itens na mesma ordem em que sairão da fila.
        for (T elemento : this) {
            // Adiciona ao resultado o item atualmente percorrido.
            lista.add(elemento);
        }
        // Devolve uma cópia sem expor os nós internos da fila.
        return lista;
    }

    @Override
    public Iterator<T> iterator() {
        // Cria um iterador que lê a fila em ordem FIFO sem removê-la.
        return new Iterator<T>() {
            // Nó que será entregue na próxima chamada de next.
            private No<T> atual = inicio;

            @Override
            public boolean hasNext() {
                // Há próximo item enquanto existir nó atual.
                return atual != null;
            }

            @Override
            public T next() {
                // Bloqueia leituras além do último elemento existente.
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                // Lê o valor e avança para o próximo nó.
                T elemento = atual.valor;
                atual = atual.proximo;
                // Retorna o item encontrado na ordem correta.
                return elemento;
            }
        };
    }
}
