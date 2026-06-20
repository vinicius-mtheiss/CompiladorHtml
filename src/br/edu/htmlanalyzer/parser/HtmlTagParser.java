package br.edu.htmlanalyzer.parser;

import br.edu.htmlanalyzer.model.ParsedTag;
import br.edu.htmlanalyzer.model.TagType;
import br.edu.htmlanalyzer.util.TagUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai tags HTML de linhas de texto, ignorando atributos na validação estrutural.
 */
public class HtmlTagParser {

    // Captura tags HTML: abertura, fechamento, comentários e declarações.
    private static final Pattern PADRAO_TAG = Pattern.compile(
            "<(!--[\\s\\S]*?--|!DOCTYPE[^>]*>|/?[a-zA-Z][a-zA-Z0-9:-]*(?:\\s+[^>]*)?/?)>",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Extrai todas as tags válidas e malformadas de uma lista de linhas.
     */
    public List<ParsedTag> extrairTags(List<String> linhas) {
        List<ParsedTag> tags = new ArrayList<>();

        for (int indice = 0; indice < linhas.size(); indice++) {
            String linha = linhas.get(indice);
            int numeroLinha = indice + 1;
            int posicao = 0;

            while (posicao < linha.length()) {
                int inicioTag = linha.indexOf('<', posicao);
                if (inicioTag < 0) {
                    break;
                }

                int fimTag = linha.indexOf('>', inicioTag);
                if (fimTag < 0) {
                    tags.add(new ParsedTag("?", TagType.ABERTURA, numeroLinha,
                            linha.substring(inicioTag)));
                    break;
                }

                String trecho = linha.substring(inicioTag, fimTag + 1);

                if (isDeclaracaoDoctype(trecho)) {
                    tags.add(new ParsedTag("!doctype", TagType.AUTOFECHAMENTO, numeroLinha, trecho));
                    posicao = fimTag + 1;
                    continue;
                }

                Matcher matcher = PADRAO_TAG.matcher(trecho);

                if (matcher.matches()) {
                    ParsedTag tag = interpretarTag(trecho, numeroLinha);
                    if (tag != null) {
                        tags.add(tag);
                    }
                } else {
                    tags.add(new ParsedTag("?", TagType.ABERTURA, numeroLinha, trecho));
                }

                posicao = fimTag + 1;
            }
        }

        return tags;
    }

    private ParsedTag interpretarTag(String trecho, int linha) {
        String interno = trecho.substring(1, trecho.length() - 1).trim();

        if (interno.startsWith("!--")) {
            return null; // Comentários são ignorados.
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
            return new ParsedTag("?", TagType.ABERTURA, linha, trecho);
        }

        if (nome.startsWith("!")) {
            nome = "!doctype";
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

    /**
     * Extrai apenas o nome da tag, descartando atributos.
     */
    private String extrairNomeTag(String conteudoTag) {
        if (conteudoTag.isEmpty()) {
            return "";
        }

        if (conteudoTag.toUpperCase().startsWith("!DOCTYPE")) {
            return "!doctype";
        }

        int fimNome = 0;
        while (fimNome < conteudoTag.length()
                && (Character.isLetterOrDigit(conteudoTag.charAt(fimNome))
                || conteudoTag.charAt(fimNome) == ':'
                || conteudoTag.charAt(fimNome) == '-'
                || conteudoTag.charAt(fimNome) == '!')) {
            fimNome++;
        }

        if (fimNome == 0) {
            return "";
        }

        return conteudoTag.substring(0, fimNome);
    }

    private boolean isDeclaracaoDoctype(String trecho) {
        return trecho.length() > 2
                && trecho.startsWith("<!")
                && trecho.substring(1).trim().toUpperCase().startsWith("!DOCTYPE")
                && trecho.endsWith(">");
    }
}
