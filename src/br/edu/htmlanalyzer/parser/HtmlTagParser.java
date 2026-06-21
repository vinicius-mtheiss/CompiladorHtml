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

    /**
     * Extrai todas as tags válidas e malformadas de uma lista de linhas.
     */
    public List<ParsedTag> extrairTags(List<String> linhas) {
        List<ParsedTag> tags = new ArrayList<>();

        for (int indice = 0; indice < linhas.size(); indice++) {
            String linha = linhas.get(indice);
            int numeroLinha = indice + 1;
            if (linha.trim().isEmpty()) {
                continue;
            }
            int posicao = 0;

            while (posicao < linha.length()) {
                int inicioTag = linha.indexOf('<', posicao);
                if (inicioTag < 0) {
                    break;
                }

                int fimTag = encontrarFimDaTag(linha, inicioTag);
                if (fimTag < 0) {
                    tags.add(new ParsedTag("?", TagType.ABERTURA, numeroLinha,
                            linha.substring(inicioTag)));
                    break;
                }

                String trecho = linha.substring(inicioTag, fimTag + 1);

                ParsedTag tag = interpretarTag(trecho, numeroLinha);
                if (tag != null) {
                    tags.add(tag);
                }

                posicao = fimTag + 1;
            }
        }

        return tags;
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
        for (int i = inicio + 1; i < linha.length(); i++) {
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
