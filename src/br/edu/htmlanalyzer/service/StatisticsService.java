package br.edu.htmlanalyzer.service;

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
        int total = 0;
        for (ParsedTag tag : tags) {
            if (deveIgnorarNaContagem(tag)) {
                continue;
            }
            total++;
        }
        return total;
    }

    /**
     * Calcula frequência, tipo predominante e primeira ocorrência de cada tag.
     */
    public List<TagStatistics> gerarEstatisticas(List<ParsedTag> tags) {
        Map<String, ContadorTag> contadores = new HashMap<>();

        for (ParsedTag tag : tags) {
            if (deveIgnorarNaContagem(tag)) {
                continue;
            }

            String chave = TagUtils.normalizar(tag.getNome());
            ContadorTag contador = contadores.computeIfAbsent(chave, ContadorTag::new);
            contador.registrar(tag);
        }

        TagStatistics[] array = contadores.values().stream()
                .map(ContadorTag::toStatistics)
                .toArray(TagStatistics[]::new);

        MergeSort.sort(array);

        List<TagStatistics> resultado = new ArrayList<>();
        for (TagStatistics estatistica : array) {
            resultado.add(estatistica);
        }
        return resultado;
    }

    private boolean deveIgnorarNaContagem(ParsedTag tag) {
        return "?".equals(tag.getNome()) || tag.getTipo() == TagType.FECHAMENTO;
    }

    private static class ContadorTag {

        private final String tag;
        private int frequencia;
        private int primeiraOcorrencia;
        private TagType tipo;

        ContadorTag(String tag) {
            this.tag = tag;
            this.frequencia = 0;
            this.primeiraOcorrencia = Integer.MAX_VALUE;
        }

        void registrar(ParsedTag parsedTag) {
            frequencia++;
            primeiraOcorrencia = Math.min(primeiraOcorrencia, parsedTag.getLinha());

            tipo = parsedTag.getTipo();
        }

        TagStatistics toStatistics() {
            return new TagStatistics(tag, frequencia, tipo, primeiraOcorrencia);
        }
    }
}
