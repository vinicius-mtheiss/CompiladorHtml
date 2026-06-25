package br.edu.htmlanalyzer.util;

/**
 * SUMÁRIO DO ARQUIVO: centraliza pequenas operações repetidas com nomes de
 * tags, como normalização, comparação e identificação de singletons.
 * POR QUE ESTÁ SEPARADO: impede que cada classe reimplemente essas regras e
 * garante que todas comparem tags com o mesmo critério.
 */

/**
 * Utilitários para normalização e classificação de tags HTML.
 */
public final class TagUtils {

    private TagUtils() {
        // Impede instanciar uma classe formada apenas por métodos e constantes utilitários.
        // Classe utilitária.
    }

    public static boolean isSingleton(String tag) {
        // Normaliza primeiro para que <BR> e <br> recebam a mesma classificação.
        String normalizada = normalizar(tag);
        // Compara caso a caso para não depender de estruturas prontas.
        return "meta".equals(normalizada)
                || "base".equals(normalizada)
                || "br".equals(normalizada)
                || "col".equals(normalizada)
                || "command".equals(normalizada)
                || "embed".equals(normalizada)
                || "hr".equals(normalizada)
                || "img".equals(normalizada)
                || "input".equals(normalizada)
                || "link".equals(normalizada)
                || "param".equals(normalizada)
                || "source".equals(normalizada)
                || "!doctype".equals(normalizada);
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
