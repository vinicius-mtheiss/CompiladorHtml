package br.edu.htmlanalyzer.service;

/**
 * SUMÁRIO DO ARQUIVO: calcula quantas vezes cada tag relevante aparece e
 * produz uma lista alfabética de TagStatistics.
 * POR QUE ESTÁ SEPARADO: regras de contagem e ordenação ficam fora do parser,
 * validador e interface, que não precisam conhecer esses detalhes.
 */

import br.edu.htmlanalyzer.datastructure.MergeSort;
import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagStatistics;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gera estatísticas de tags e ordena alfabeticamente com MergeSort.
 */
public class StatisticsService {

    /**
     * Conta tags de abertura e autofechamento, ignorando fechamentos e malformadas.
     */
    public int contarTags(List<ParsedTag> tags) {
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
    public List<TagStatistics> gerarEstatisticas(List<ParsedTag> tags) {
        // Mapeia cada nome de tag para o acumulador de suas informações.
        Map<String, ContadorTag> contadores = new HashMap<>();

        // Percorre as tags em sua ordem de aparecimento no arquivo.
        for (ParsedTag tag : tags) {
            // Usa exatamente o mesmo filtro aplicado ao total para manter os números coerentes.
            if (deveIgnorarNaContagem(tag)) {
                continue;
            }

            // Normaliza o nome, de forma que DIV e div acumulem no mesmo grupo.
            String chave = TagUtils.normalizar(tag.getNome());
            // Cria o contador somente na primeira ocorrência da chave.
            ContadorTag contador = contadores.computeIfAbsent(chave, ContadorTag::new);
            // Atualiza frequência, primeira linha e tipo com a ocorrência atual.
            contador.registrar(tag);
        }

        // Converte cada acumulador em um objeto imutável apropriado para apresentação.
        TagStatistics[] array = contadores.values().stream()
                .map(ContadorTag::toStatistics)
                .toArray(TagStatistics[]::new);

        // Ordena alfabeticamente usando a implementação manual de MergeSort.
        MergeSort.sort(array);

        // Cria a lista final a partir do array já ordenado.
        List<TagStatistics> resultado = new ArrayList<>();
        // Mantém a ordem produzida pela ordenação ao copiar cada estatística.
        for (TagStatistics estatistica : array) {
            resultado.add(estatistica);
        }
        // Devolve a coleção que o relatório e a interface poderão percorrer.
        return resultado;
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

        // Cria um acumulador recém-descoberto no mapa.
        ContadorTag(String tag) {
            this.tag = tag;
            this.frequencia = 0;
            this.primeiraOcorrencia = Integer.MAX_VALUE;
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
