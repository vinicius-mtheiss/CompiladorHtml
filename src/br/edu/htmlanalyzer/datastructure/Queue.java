package br.edu.htmlanalyzer.datastructure;

/**
 * SUMÁRIO DO ARQUIVO: implementa uma fila genérica, que devolve primeiro o
 * elemento que entrou primeiro (regra FIFO), usando um array circular.
 * POR QUE ESTÁ SEPARADO: o validador só precisa registrar erros em ordem;
 * os detalhes de armazenar e redimensionar a fila ficam encapsulados aqui.
 */

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

    // Reserva inicial para evitar redimensionar a cada inclusão.
    private static final int CAPACIDADE_INICIAL = 16;

    // Guarda os itens em posições físicas do array circular.
    private Object[] elementos;
    // Indica a posição do próximo elemento que sairá da fila.
    private int inicio;
    // Indica a posição do último elemento que entrou; começa em -1 porque a fila nasce vazia.
    private int fim;
    // Guarda a quantidade real de itens, separando o caso vazio do caso cheio no array circular.
    private int quantidade;

    // Inicializa uma fila vazia e seus ponteiros de controle.
    public Queue() {
        // Aloca o armazenamento inicial da fila.
        this.elementos = new Object[CAPACIDADE_INICIAL];
        // O primeiro elemento futuro ocupará a posição zero.
        this.inicio = 0;
        // Ainda não houve último elemento inserido.
        this.fim = -1;
        // Nenhum elemento está na fila no começo.
        this.quantidade = 0;
    }

    // Acrescenta um elemento no fim, preservando a ordem FIFO.
    public void enqueue(T elemento) {
        // Quando todas as posições estão ocupadas, amplia o array antes da inserção.
        if (quantidade == elementos.length) {
            redimensionar();
        }
        // Avança circularmente: depois do último índice, retorna à posição zero.
        fim = (fim + 1) % elementos.length;
        // Grava o novo item na nova posição final.
        elementos[fim] = elemento;
        // Registra que a fila passou a ter mais um item.
        quantidade++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        // Remover de uma fila vazia não tem resultado válido.
        if (isEmpty()) {
            // Explica a causa da falha ao código que chamou a operação.
            throw new NoSuchElementException("Fila vazia.");
        }
        // Lê e converte o elemento mais antigo, localizado no início.
        T removido = (T) elementos[inicio];
        // Apaga a referência armazenada para que o objeto possa ser coletado, se necessário.
        elementos[inicio] = null;
        // Move o início circularmente para o próximo elemento.
        inicio = (inicio + 1) % elementos.length;
        // Diminui a quantidade real de elementos presentes.
        quantidade--;
        // Devolve o primeiro item que havia entrado na fila.
        return removido;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        // Não existe primeiro elemento quando a fila está vazia.
        if (isEmpty()) {
            throw new NoSuchElementException("Fila vazia.");
        }
        // Consulta o primeiro item sem removê-lo nem mover os ponteiros.
        return (T) elementos[inicio];
    }

    // Informa se a quantidade controlada chegou a zero.
    public boolean isEmpty() {
        return quantidade == 0;
    }

    // Fornece a quantidade de elementos sem precisar percorrer o array.
    public int size() {
        return quantidade;
    }

    /**
     * Copia os elementos da fila para uma lista, preservando a ordem FIFO.
     */
    public List<T> toList() {
        // Cria uma lista com capacidade suficiente para evitar expansões internas desnecessárias.
        List<T> lista = new ArrayList<>(quantidade);
        // Usa o iterador para copiar os itens na mesma ordem em que sairão da fila.
        for (T elemento : this) {
            // Adiciona ao resultado o item atualmente percorrido.
            lista.add(elemento);
        }
        // Devolve uma cópia em List, sem expor a estrutura circular interna.
        return lista;
    }

    // Reorganiza os itens em um array duas vezes maior quando a fila está cheia.
    private void redimensionar() {
        // Reserva o novo espaço com o dobro da capacidade atual.
        Object[] novoArray = new Object[elementos.length * 2];
        // Copia em ordem lógica FIFO, começando pelo início mesmo que ele esteja no meio do array antigo.
        for (int i = 0; i < quantidade; i++) {
            novoArray[i] = elementos[(inicio + i) % elementos.length];
        }
        // Substitui o armazenamento antigo pelo array já reorganizado.
        elementos = novoArray;
        // Como a cópia começou na posição zero, o início lógico agora também é zero.
        inicio = 0;
        // O último elemento fica imediatamente antes da quantidade total.
        fim = quantidade - 1;
    }

    @Override
    public Iterator<T> iterator() {
        // Cria um iterador que lê a fila em ordem FIFO sem removê-la.
        return new Iterator<T>() {
            // Conta quantos itens lógicos já foram entregues pelo iterador.
            private int indice = 0;

            @Override
            public boolean hasNext() {
                // Há próximo item enquanto não atingirmos a quantidade total da fila.
                return indice < quantidade;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                // Bloqueia leituras além do último elemento existente.
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                // Traduz o índice lógico para a posição física circular do array.
                T elemento = (T) elementos[(inicio + indice) % elementos.length];
                // Prepara a leitura do próximo elemento na chamada seguinte.
                indice++;
                // Retorna o item encontrado na ordem correta.
                return elemento;
            }
        };
    }
}
