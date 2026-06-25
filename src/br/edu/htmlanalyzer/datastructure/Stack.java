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
 * Implementação manual de uma Pilha (LIFO) baseada em nós encadeados.
 *
 * @param <T> tipo dos elementos armazenados
 */
public class Stack<T> implements Iterable<T> {

    // Nó interno que guarda um elemento e aponta para o nó que estava abaixo dele.
    private static class No<T> {

        // Valor armazenado neste nível da pilha.
        private final T valor;
        // Próximo nó em direção à base da pilha.
        private final No<T> anterior;

        // Liga o novo topo ao topo anterior.
        No(T valor, No<T> anterior) {
            this.valor = valor;
            this.anterior = anterior;
        }
    }

    // Referência direta ao último elemento inserido.
    private No<T> topo;
    // Quantidade de elementos presentes na pilha.
    private int quantidade;

    // Cria uma pilha vazia, sem reservar arrays ou coleções prontas.
    public Stack() {
        this.topo = null;
        this.quantidade = 0;
    }

    // Insere um elemento no topo, mantendo a regra "último a entrar, primeiro a sair".
    public void push(T elemento) {
        topo = new No<>(elemento, topo);
        quantidade++;
    }

    // Remove e devolve o item do topo da pilha.
    public T pop() {
        // Não há elemento para remover quando a pilha está vazia.
        if (isEmpty()) {
            // A exceção deixa claro para quem chamou que a operação não é permitida.
            throw new EmptyStackException();
        }
        // Guarda o valor antes de recuar a referência do topo.
        T removido = topo.valor;
        // O nó abaixo passa a ser o novo topo.
        topo = topo.anterior;
        // Registra que a pilha perdeu um elemento.
        quantidade--;
        // Entrega ao chamador o elemento que estava no topo.
        return removido;
    }

    public T peek() {
        // Consultar o topo também é inválido quando nenhum item foi inserido.
        if (isEmpty()) {
            // Mantém o mesmo contrato de erro de pop para uma pilha vazia.
            throw new EmptyStackException();
        }
        // Retorna o valor do topo sem alterar a pilha.
        return topo.valor;
    }

    // Informa se não existe nó no topo.
    public boolean isEmpty() {
        return topo == null;
    }

    // Devolve a quantidade controlada sem percorrer os nós.
    public int size() {
        return quantidade;
    }

    /**
     * Retorna uma cópia dos elementos atuais da pilha (do topo para a base).
     */
    public Lista<T> toLista() {
        // Cria uma lista manual para não expor os nós internos da pilha.
        Lista<T> copia = new Lista<>();
        // O iterador já percorre do topo para a base.
        for (T elemento : this) {
            copia.add(elemento);
        }
        // Devolve uma coleção independente da pilha.
        return copia;
    }

    @Override
    public Iterator<T> iterator() {
        // Cria um iterador anônimo que percorre a pilha do topo até a base.
        return new Iterator<T>() {
            // Começa no topo para respeitar a ordem natural de leitura da pilha.
            private No<T> atual = topo;

            @Override
            public boolean hasNext() {
                // Ainda há item enquanto existir um nó atual.
                return atual != null;
            }

            @Override
            public T next() {
                // Impede a leitura depois que todos os elementos já foram entregues.
                if (!hasNext()) {
                    // Segue o contrato de Iterator para informar que não existe próximo item.
                    throw new NoSuchElementException();
                }
                // Lê o valor e recua para o nó abaixo na pilha.
                T valor = atual.valor;
                atual = atual.anterior;
                return valor;
            }
        };
    }
}
