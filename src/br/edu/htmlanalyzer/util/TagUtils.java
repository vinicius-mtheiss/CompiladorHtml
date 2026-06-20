package br.edu.htmlanalyzer.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilitários para normalização e classificação de tags HTML.
 */
public final class TagUtils {

    private static final Set<String> TAGS_SINGLETON = new HashSet<>(Arrays.asList(
            "meta", "base", "br", "col", "command", "embed", "hr",
            "img", "input", "link", "param", "source", "!doctype"
    ));

    private TagUtils() {
        // Classe utilitária.
    }

    public static boolean isSingleton(String tag) {
        return TAGS_SINGLETON.contains(normalizar(tag));
    }

    public static String normalizar(String tag) {
        if (tag == null) {
            return "";
        }
        return tag.trim().toLowerCase();
    }

    public static boolean tagsEquivalentes(String tagA, String tagB) {
        return normalizar(tagA).equals(normalizar(tagB));
    }
}
