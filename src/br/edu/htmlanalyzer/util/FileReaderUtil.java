package br.edu.htmlanalyzer.util;

/**
 * SUMÁRIO DO ARQUIVO: lê um arquivo texto mantendo suas linhas e verifica se
 * o caminho aponta para uma extensão aceita pelo programa.
 * POR QUE ESTÁ SEPARADO: detalhes de sistema de arquivos não se misturam às
 * regras HTML; assim o serviço de análise recebe simplesmente uma lista de linhas.
 */

import br.edu.htmlanalyzer.datastructure.Lista;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utilitário para leitura de arquivos linha a linha.
 */
public final class FileReaderUtil {

    // Impede instâncias porque esta classe só agrupa operações estáticas de arquivo.
    private FileReaderUtil() {
        // Classe utilitária.
    }

    /**
     * Lê o arquivo linha a linha. Linhas em branco são preservadas para que a
     * numeração exibida nos erros corresponda ao arquivo selecionado.
     */
    public static Lista<String> lerLinhas(String caminho) throws IOException {
        // Cria a lista que preservará inclusive linhas em branco.
        Lista<String> linhas = new Lista<>();
        // Transforma o texto do caminho em um objeto que permite consultar o sistema de arquivos.
        File arquivo = new File(caminho);

        // Diferencia caminho inexistente de outros problemas de leitura para orientar o usuário.
        if (!arquivo.exists()) {
            throw new IOException("Arquivo não encontrado: " + caminho);
        }

        // Impede tentar abrir um diretório como se fosse um arquivo de texto.
        if (!arquivo.isFile()) {
            throw new IOException("O caminho informado não é um arquivo: " + caminho);
        }

        // Abre o leitor e garante seu fechamento automático mesmo se ocorrer uma exceção.
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            // Declara a variável que receberá uma linha por iteração.
            String linha;
            // Lê até readLine retornar null, sinalizando o fim do arquivo.
            while ((linha = reader.readLine()) != null) {
                // Adiciona cada linha exatamente na ordem original.
                linhas.add(linha);
            }
        }
        // Entrega as linhas para que o parser possa percorrê-las.
        return linhas;
    }

    // Verifica pelo sufixo se o caminho pertence aos formatos permitidos pela aplicação.
    public static boolean isExtensaoValida(String caminho) {
        // Um caminho ausente não tem como possuir extensão válida.
        if (caminho == null) {
            return false;
        }
        // Padroniza maiúsculas/minúsculas para aceitar, por exemplo, .HTML.
        String nome = caminho.toLowerCase();
        // Aceita as três extensões de texto que o analisador foi projetado para ler.
        return nome.endsWith(".html") || nome.endsWith(".htm") || nome.endsWith(".txt");
    }
}
