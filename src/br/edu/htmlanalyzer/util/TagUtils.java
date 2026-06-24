package br.edu.htmlanalyzer.util;

/**
 * SUMÁRIO DO ARQUIVO: centraliza pequenas operações repetidas com nomes de
 * tags, como normalização, comparação e identificação de singletons.
 * POR QUE ESTÁ SEPARADO: impede que cada classe reimplemente essas regras e
 * garante que todas comparem tags com o mesmo critério.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilitários para normalização e classificação de tags HTML.
 */
public final class TagUtils {

    // Reúne nomes de tags HTML que não exigem uma tag de fechamento separada.
    private static final Set<String> TAGS_SINGLETON = new HashSet<>(Arrays.asList(
            "meta", "base", "br", "col", "command", "embed", "hr",
            "img", "input", "link", "param", "source", "!doctype"
    ));

    private TagUtils() {
        // Impede instanciar uma classe formada apenas por métodos e constantes utilitários.
        // Classe utilitária.
    }

    public static boolean isSingleton(String tag) {
        // Normaliza primeiro para que <BR> e <br> recebam a mesma classificação.
        return TAGS_SINGLETON.contains(normalizar(tag));
    }

    // Remove espaços extras e reduz para minúsculas, criando uma forma única de comparar tags.
    public static String normalizar(String tag) {
        // Evita NullPointerException e estabelece string vazia como equivalente para ausência de nome.
        if (tag == null) {
            return "";
        }
        // Elimina espaços externos e torna a comparação independente de maiúsculas/minúsculas.
        return tag.trim().toLowerCase();
    }

    // Compara duas tags usando a forma normalizada de ambas.
    public static boolean tagsEquivalentes(String tagA, String tagB) {
        return normalizar(tagA).equals(normalizar(tagB));
    }
}
