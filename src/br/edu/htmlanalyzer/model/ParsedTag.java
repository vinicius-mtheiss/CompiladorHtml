package br.edu.htmlanalyzer.model;

/**
 * SUMÁRIO DO ARQUIVO: descreve uma tag que o parser extraiu do texto, junto
 * com o tipo, a linha de origem e a escrita original.
 * POR QUE ESTÁ SEPARADO: parser e consumidores posteriores concordam em um
 * formato único de tag, sem precisar reinterpretar o texto do arquivo.
 */

/**
 * Tag extraída do documento com metadados de posição.
 */
public class ParsedTag {

    // Nome da tag sem os delimitadores < e > e sem seus atributos.
    private final String nome;
    // Papel da tag na estrutura do documento.
    private final TagType tipo;
    // Número da linha onde a tag começou no arquivo original.
    private final int linha;
    // Texto da tag como foi lido, útil para informar uma tag malformada.
    private final String original;

    // Monta uma representação imutável de uma tag identificada pelo parser.
    public ParsedTag(String nome, TagType tipo, int linha, String original) {
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
        this.original = original;
    }

    public String getNome() {
        // Retorna o nome usado para comparação estrutural.
        return nome;
    }

    public TagType getTipo() {
        // Retorna a classificação escolhida durante a interpretação.
        return tipo;
    }

    public int getLinha() {
        // Retorna a posição inicial para mensagens de erro precisas.
        return linha;
    }

    public String getOriginal() {
        // Retorna o trecho bruto preservado do arquivo.
        return original;
    }
}
