package br.edu.htmlanalyzer.datastructure;

/**
 * SUMÁRIO DO ARQUIVO: implementa uma pilha genérica, isto é, uma coleção em
 * que o último elemento inserido é o primeiro removido (regra LIFO).
 * POR QUE ESTÁ SEPARADO: a pilha é uma estrutura reutilizável; deixá-la fora
 * do validador evita misturar a regra da estrutura de dados com as regras HTML.
 */

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementação manual de uma Pilha (LIFO) baseada em array dinâmico.
 *
 * @param <T> tipo dos elementos armazenados
 */
public class Stack<T> implements Iterable<T> {

    // Define quantas posições o array interno possui ao nascer.
    private static final int CAPACIDADE_INICIAL = 16;

    // Guarda os elementos; Object[] é usado porque arrays genéricos não podem ser criados diretamente em Java.
    private Object[] elementos;
    // Armazena o índice do último elemento inserido; -1 representa uma pilha vazia.
    private int topo;

    // Cria uma pilha vazia e reserva a capacidade inicial para evitar realocações precoces.
    public Stack() {
        // Aloca as posições que guardarão os itens da pilha.
        this.elementos = new Object[CAPACIDADE_INICIAL];
        // Marca que ainda não existe elemento no topo.
        this.topo = -1;
    }

    // Insere um elemento no topo, mantendo a regra "último a entrar, primeiro a sair".
    public void push(T elemento) {
        // Se o topo já é a última posição disponível, é preciso aumentar o array antes de gravar.
        if (topo == elementos.length - 1) {
            // Duplica a capacidade preservando os itens existentes.
            redimensionar();
        }
        // Avança o topo e coloca o elemento exatamente nessa nova posição.
        elementos[++topo] = elemento;
    }

    // Avisa ao compilador que o cast do Object guardado para T foi revisado neste ponto.
    @SuppressWarnings("unchecked")
    // Remove e devolve o item do topo da pilha.
    public T pop() {
        // Não há elemento para remover quando a pilha está vazia.
        if (isEmpty()) {
            // A exceção deixa claro para quem chamou que a operação não é permitida.
            throw new EmptyStackException();
        }
        // Lê o elemento atual do topo e o converte de Object de volta para T.
        T removido = (T) elementos[topo];
        // Libera a referência e só então diminui o topo para permitir coleta de lixo do objeto removido.
        elementos[topo--] = null;
        // Entrega ao chamador o elemento que estava no topo.
        return removido;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        // Consultar o topo também é inválido quando nenhum item foi inserido.
        if (isEmpty()) {
            // Mantém o mesmo contrato de erro de pop para uma pilha vazia.
            throw new EmptyStackException();
        }
        // Retorna o topo sem alterar índice nem conteúdo da pilha.
        return (T) elementos[topo];
    }

    // Informa se o índice do topo ainda indica a posição anterior ao início do array.
    public boolean isEmpty() {
        return topo < 0;
    }

    // Converte o índice do topo em quantidade de elementos, pois o índice começa em zero.
    public int size() {
        return topo + 1;
    }

    /**
     * Retorna uma cópia dos elementos atuais da pilha (do topo para a base).
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        // Cria uma cópia com apenas as posições ocupadas da pilha.
        T[] copia = (T[]) new Object[size()];
        // Visita cada posição que vai da base (0) até o topo atual.
        for (int i = 0; i <= topo; i++) {
            // Copia o elemento sem expor o array interno da estrutura.
            copia[i] = (T) elementos[i];
        }
        // Devolve a nova coleção independente da pilha.
        return copia;
    }

    // Aumenta a capacidade interna quando a pilha não cabe mais no array atual.
    private void redimensionar() {
        // Cria um array com o dobro das posições para reduzir a frequência de futuras cópias.
        Object[] novoArray = new Object[elementos.length * 2];
        // Copia todos os elementos existentes para as mesmas posições do novo array.
        System.arraycopy(elementos, 0, novoArray, 0, elementos.length);
        // Faz a pilha passar a usar o array maior.
        elementos = novoArray;
    }

    @Override
    public Iterator<T> iterator() {
        // Cria um iterador anônimo que percorre a pilha do topo até a base.
        return new Iterator<T>() {
            // Começa no topo para respeitar a ordem natural de leitura da pilha.
            private int indice = topo;

            @Override
            public boolean hasNext() {
                // Ainda há item enquanto o índice não passou da posição zero.
                return indice >= 0;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                // Impede a leitura depois que todos os elementos já foram entregues.
                if (!hasNext()) {
                    // Segue o contrato de Iterator para informar que não existe próximo item.
                    throw new NoSuchElementException();
                }
                // Retorna o item atual e reduz o índice para preparar a próxima chamada.
                return (T) elementos[indice--];
            }
        };
    }
}
