package br.edu.htmlanalyzer.parser;

import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Extrai tags HTML de linhas de texto, ignorando atributos na validação estrutural.
 */
public class HtmlTagParser {

    private static final class TagExtraction {

        private final String trecho;
        private final int linhaInicio;
        private final int indiceLinhaFinal;
        private final int posicaoFinal;

        private TagExtraction(String trecho, int linhaInicio, int indiceLinhaFinal, int posicaoFinal) {
            this.trecho = trecho;
            this.linhaInicio = linhaInicio;
            this.indiceLinhaFinal = indiceLinhaFinal;
            this.posicaoFinal = posicaoFinal;
        }
    }

    /**
     * Extrai todas as tags válidas e malformadas de uma lista de linhas.
     */
    public List<ParsedTag> extrairTags(List<String> linhas) {
        List<ParsedTag> tags = new ArrayList<>();
        int indice = 0;
        int posicao = 0;

        while (indice < linhas.size()) {
            String linha = linhas.get(indice);
            if (linha.trim().isEmpty()) {
                indice++;
                posicao = 0;
                continue;
            }

            if (posicao >= linha.length()) {
                indice++;
                posicao = 0;
                continue;
            }

            int inicioTag = linha.indexOf('<', posicao);
            if (inicioTag < 0) {
                indice++;
                posicao = 0;
                continue;
            }

            TagExtraction extracao = extrairTagCompleta(linhas, indice, inicioTag);

            if (extracao.posicaoFinal < 0) {
                tags.add(new ParsedTag("?", TagType.ABERTURA, extracao.linhaInicio, extracao.trecho));
                if (extracao.indiceLinhaFinal > indice) {
                    indice = extracao.indiceLinhaFinal + 1;
                    posicao = 0;
                } else {
                    posicao = inicioTag + 1;
                }
                continue;
            }

            ParsedTag tag = interpretarTag(extracao.trecho, extracao.linhaInicio);
            if (tag != null) {
                tags.add(tag);
            }

            indice = extracao.indiceLinhaFinal;
            posicao = extracao.posicaoFinal;
        }

        return tags;
    }

    private TagExtraction extrairTagCompleta(List<String> linhas, int indiceInicial, int inicioTag) {
        String linha = linhas.get(indiceInicial);
        int linhaInicio = indiceInicial + 1;
        int fimTag = encontrarFimDaTag(linha, inicioTag);

        if (fimTag >= 0) {
            return new TagExtraction(
                    linha.substring(inicioTag, fimTag + 1),
                    linhaInicio,
                    indiceInicial,
                    fimTag + 1
            );
        }

        StringBuilder trecho = new StringBuilder(linha.substring(inicioTag));
        int indice = indiceInicial;

        while (indice + 1 < linhas.size()) {
            indice++;
            String proxima = linhas.get(indice);
            if (proxima.trim().startsWith("<")) {
                return new TagExtraction(trecho.toString(), linhaInicio, indiceInicial, -1);
            }

            fimTag = encontrarFimDaTag(proxima, -1);
            if (fimTag >= 0) {
                trecho.append('\n').append(proxima, 0, fimTag + 1);
                return new TagExtraction(
                        trecho.toString(),
                        linhaInicio,
                        indice,
                        fimTag + 1
                );
            }

            trecho.append('\n').append(proxima);
        }

        return new TagExtraction(trecho.toString(), linhaInicio, indice, -1);
    }

    private ParsedTag interpretarTag(String trecho, int linha) {
        String interno = trecho.substring(1, trecho.length() - 1).trim();

        if (interno.startsWith("!--")) {
            return interno.endsWith("--") ? null : tagMalformada(trecho, linha);
        }

        if (interno.regionMatches(true, 0, "!doctype", 0, 8)) {
            return interno.matches("(?i)!doctype(?:\\s+[^<>]+)?")
                    ? new ParsedTag("!doctype", TagType.AUTOFECHAMENTO, linha, trecho)
                    : tagMalformada(trecho, linha);
        }

        boolean fechamento = interno.startsWith("/");
        if (fechamento) {
            interno = interno.substring(1).trim();
        }

        boolean autofechamento = interno.endsWith("/");
        if (autofechamento) {
            interno = interno.substring(0, interno.length() - 1).trim();
        }

        String nome = extrairNomeTag(interno);
        if (nome.isEmpty()) {
            return tagMalformada(trecho, linha);
        }

        String restante = interno.substring(nome.length()).trim();
        if (fechamento && (!restante.isEmpty() || autofechamento)) {
            return tagMalformada(trecho, linha);
        }
        if (!fechamento && restante.contains("<")) {
            return tagMalformada(trecho, linha);
        }

        TagType tipo;
        if (fechamento) {
            tipo = TagType.FECHAMENTO;
        } else if (autofechamento || TagUtils.isSingleton(nome)) {
            tipo = TagType.AUTOFECHAMENTO;
        } else {
            tipo = TagType.ABERTURA;
        }

        return new ParsedTag(nome, tipo, linha, trecho);
    }

    private int encontrarFimDaTag(String linha, int inicio) {
        char aspas = 0;
        int inicioBusca = inicio < 0 ? 0 : inicio + 1;
        for (int i = inicioBusca; i < linha.length(); i++) {
            char caractere = linha.charAt(i);
            if (aspas != 0) {
                if (caractere == aspas) {
                    aspas = 0;
                }
            } else if (caractere == '\'' || caractere == '"') {
                aspas = caractere;
            } else if (caractere == '>') {
                return i;
            }
        }
        return -1;
    }

    private ParsedTag tagMalformada(String trecho, int linha) {
        return new ParsedTag("?", TagType.ABERTURA, linha, trecho);
    }

    /**
     * Extrai apenas o nome da tag, descartando atributos.
     */
    private String extrairNomeTag(String conteudoTag) {
        if (conteudoTag.isEmpty()) {
            return "";
        }

        int fimNome = 0;
        while (fimNome < conteudoTag.length()
                && (Character.isLetterOrDigit(conteudoTag.charAt(fimNome))
                || conteudoTag.charAt(fimNome) == ':'
                || conteudoTag.charAt(fimNome) == '-')) {
            fimNome++;
        }

        if (fimNome == 0) {
            return "";
        }

        return conteudoTag.substring(0, fimNome);
    }

}
