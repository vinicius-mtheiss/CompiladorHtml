package br.edu.htmlanalyzer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitário para leitura de arquivos linha a linha.
 */
public final class FileReaderUtil {

    private FileReaderUtil() {
        // Classe utilitária.
    }

    /**
     * Lê o arquivo linha a linha. Linhas em branco são preservadas para que a
     * numeração exibida nos erros corresponda ao arquivo selecionado.
     */
    public static List<String> lerLinhas(String caminho) throws IOException {
        List<String> linhas = new ArrayList<>();
        File arquivo = new File(caminho);

        if (!arquivo.exists()) {
            throw new IOException("Arquivo não encontrado: " + caminho);
        }

        if (!arquivo.isFile()) {
            throw new IOException("O caminho informado não é um arquivo: " + caminho);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linhas.add(linha);
            }
        }
        return linhas;
    }

    public static boolean isExtensaoValida(String caminho) {
        if (caminho == null) {
            return false;
        }
        String nome = caminho.toLowerCase();
        return nome.endsWith(".html") || nome.endsWith(".htm") || nome.endsWith(".txt");
    }
}
