package br.edu.htmlanalyzer.service;

/**
 * SUMÁRIO DO ARQUIVO: calcula quantas vezes cada tag relevante aparece e
 * produz uma lista alfabética de TagStatistics.
 * POR QUE ESTÁ SEPARADO: regras de contagem e ordenação ficam fora do parser,
 * validador e interface, que não precisam conhecer esses detalhes.
 */

import br.edu.htmlanalyzer.datastructure.Lista;
import br.edu.htmlanalyzer.datastructure.MergeSort;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagStatistics;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

/**
 * Gera estatísticas de tags e ordena alfabeticamente com MergeSort.
 */
public class StatisticsService {

    /**
     * Conta tags de abertura e autofechamento, ignorando fechamentos e malformadas.
     */
    public int contarTags(Lista<ParsedTag> tags) {
        // Começa a soma sem nenhuma tag contabilizada.
        int total = 0;
        // Examina cada tag que o parser encontrou no documento.
        for (ParsedTag tag : tags) {
            // Fecha tags e tags malformadas não representam elementos a contabilizar.
            if (deveIgnorarNaContagem(tag)) {
                continue;
            }
            // Registra uma abertura ou singleton válido.
            total++;
        }
        // Devolve a quantidade final ao resultado da análise.
        return total;
    }

    /**
     * Calcula frequência, tipo predominante e primeira ocorrência de cada tag.
     */
    public Lista<TagStatistics> gerarEstatisticas(Lista<ParsedTag> tags) {
        // Guarda os acumuladores em lista própria, substituindo o antigo mapa pronto.
        Lista<ContadorTag> contadores = new Lista<>();

        // Percorre as tags em sua ordem de aparecimento no arquivo.
        for (ParsedTag tag : tags) {
            // Usa exatamente o mesmo filtro aplicado ao total para manter os números coerentes.
            if (deveIgnorarNaContagem(tag)) {
                continue;
            }

            // Normaliza o nome, de forma que DIV e div acumulem no mesmo grupo.
            String chave = TagUtils.normalizar(tag.getNome());
            // Busca manualmente o contador correspondente, sem mapa pronto do Java.
            ContadorTag contador = localizarContador(contadores, chave);
            // Cria o contador somente na primeira ocorrência da chave.
            if (contador == null) {
                contador = new ContadorTag(chave);
                contadores.add(contador);
            }
            // Atualiza frequência, primeira linha e tipo com a ocorrência atual.
            contador.registrar(tag);
        }

        // Converte cada acumulador em um objeto imutável apropriado para apresentação.
        Lista<TagStatistics> resultado = new Lista<>();
        for (ContadorTag contador : contadores) {
            resultado.add(contador.toStatistics());
        }

        // Ordena alfabeticamente usando a implementação manual de MergeSort para lista própria.
        MergeSort.sort(resultado);

        // Devolve a coleção que o relatório e a interface poderão percorrer.
        return resultado;
    }

    // Procura em lista encadeada o contador de uma tag normalizada.
    private ContadorTag localizarContador(Lista<ContadorTag> contadores, String chave) {
        for (ContadorTag contador : contadores) {
            if (contador.temTag(chave)) {
                return contador;
            }
        }
        return null;
    }

    // Decide se uma tag não deve entrar em totais nem em estatísticas.
    private boolean deveIgnorarNaContagem(ParsedTag tag) {
        // Ignora a marca interna de erro do parser e as tags que apenas fecham um elemento já contado.
        return "?".equals(tag.getNome()) || tag.getTipo() == TagType.FECHAMENTO;
    }

    private static class ContadorTag {

        // Nome normalizado que identifica o grupo acumulado.
        private final String tag;
        // Número de ocorrências desse grupo até o momento.
        private int frequencia;
        // Menor linha observada para a tag; começa no maior valor para que Math.min funcione.
        private int primeiraOcorrencia;
        // Tipo encontrado para a tag contabilizada.
        private TagType tipo;

        // Cria um acumulador recém-descoberto na lista.
        ContadorTag(String tag) {
            this.tag = tag;
            this.frequencia = 0;
            this.primeiraOcorrencia = Integer.MAX_VALUE;
        }

        // Informa se este acumulador pertence à tag procurada.
        boolean temTag(String chave) {
            return tag.equals(chave);
        }

        void registrar(ParsedTag parsedTag) {
            // Soma a ocorrência que está sendo processada.
            frequencia++;
            // Conserva o menor número de linha entre todas as ocorrências.
            primeiraOcorrencia = Math.min(primeiraOcorrencia, parsedTag.getLinha());

            // Guarda o tipo da ocorrência, usado na apresentação da estatística.
            tipo = parsedTag.getTipo();
        }

        // Converte o acumulador mutável em uma descrição final imutável.
        TagStatistics toStatistics() {
            return new TagStatistics(tag, frequencia, tipo, primeiraOcorrencia);
        }
    }
}
