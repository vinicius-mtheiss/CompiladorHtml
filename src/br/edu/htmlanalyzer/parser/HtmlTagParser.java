package br.edu.htmlanalyzer.parser;

/**
 * SUMÁRIO DO ARQUIVO: percorre as linhas do arquivo e converte cada trecho
 * entre '<' e '>' em um objeto ParsedTag, inclusive quando há erro.
 * POR QUE ESTÁ SEPARADO: concentra o conhecimento léxico do HTML e entrega ao
 * validador uma representação simples, sem misturar leitura e validação.
 */

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

        // Guarda o texto entre o '<' inicial e o '>' final, possivelmente atravessando linhas.
        private final String trecho;
        // Guarda a linha humana (iniciada em 1) em que o trecho começou.
        private final int linhaInicio;
        // Guarda o índice da última linha consumida para retomar a varredura depois dela.
        private final int indiceLinhaFinal;
        // Guarda a próxima posição da última linha a partir da qual a busca continuará.
        private final int posicaoFinal;

        // Reúne os metadados necessários para continuar a leitura após uma extração.
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
        // Cria a coleção ordenada que será entregue ao validador.
        List<ParsedTag> tags = new ArrayList<>();
        // Controla a linha atual por índice, que em List começa em zero.
        int indice = 0;
        // Controla a posição dentro da linha para encontrar mais de uma tag na mesma linha.
        int posicao = 0;

        // Continua enquanto ainda houver uma linha para examinar.
        while (indice < linhas.size()) {
            // Obtém o texto da linha atual usando o índice controlado pelo laço.
            String linha = linhas.get(indice);
            // Linhas em branco não contêm tags, mas seu índice é preservado para as mensagens.
            if (linha.trim().isEmpty()) {
                indice++;
                posicao = 0;
                continue;
            }

            // Quando a posição chegou ao fim da linha, avança para a linha seguinte.
            if (posicao >= linha.length()) {
                indice++;
                posicao = 0;
                continue;
            }

            // Procura o próximo possível começo de tag a partir da posição ainda não lida.
            int inicioTag = linha.indexOf('<', posicao);
            // Sem '<' restante, não existe outra tag nesta linha.
            if (inicioTag < 0) {
                indice++;
                posicao = 0;
                continue;
            }

            // Obtém o trecho completo e seus metadados, inclusive para tags em várias linhas.
            TagExtraction extracao = extrairTagCompleta(linhas, indice, inicioTag);

            // Posição negativa indica que não foi encontrado um '>' que complete a tag.
            if (extracao.posicaoFinal < 0) {
                // Registra uma tag especial de erro para que o validador produza o diagnóstico adequado.
                tags.add(new ParsedTag("?", TagType.ABERTURA, extracao.linhaInicio, extracao.trecho));
                // Se a procura consumiu linhas, pula diretamente para depois do trecho problemático.
                if (extracao.indiceLinhaFinal > indice) {
                    indice = extracao.indiceLinhaFinal + 1;
                    posicao = 0;
                // Caso contrário, anda um caractere para procurar outro '<' na mesma linha.
                } else {
                    posicao = inicioTag + 1;
                }
                continue;
            }

            // Classifica o trecho sintaticamente completo como abertura, fechamento, singleton ou erro.
            ParsedTag tag = interpretarTag(extracao.trecho, extracao.linhaInicio);
            // Comentários bem formados não viram tags e são representados por null.
            if (tag != null) {
                tags.add(tag);
            }

            // Retoma a varredura exatamente após o trecho consumido.
            indice = extracao.indiceLinhaFinal;
            posicao = extracao.posicaoFinal;
        }

        return tags;
    }

    private TagExtraction extrairTagCompleta(List<String> linhas, int indiceInicial, int inicioTag) {
        // Lê a linha onde o sinal '<' foi encontrado.
        String linha = linhas.get(indiceInicial);
        // Converte o índice interno de zero para o número de linha que o usuário vê.
        int linhaInicio = indiceInicial + 1;
        // Busca o '>' correspondente sem confundi-lo com caracteres dentro de aspas.
        int fimTag = encontrarFimDaTag(linha, inicioTag);

        // Se o fim está na própria linha, constrói e devolve a extração imediatamente.
        if (fimTag >= 0) {
            return new TagExtraction(
                    linha.substring(inicioTag, fimTag + 1),
                    linhaInicio,
                    indiceInicial,
                    fimTag + 1
            );
        }

        // Inicia o trecho com tudo que existe depois do '<' na primeira linha.
        StringBuilder trecho = new StringBuilder(linha.substring(inicioTag));
        // Controla as linhas extras examinadas na tentativa de completar a tag.
        int indice = indiceInicial;

        // Tenta encontrar o fechamento em cada linha seguinte existente.
        while (indice + 1 < linhas.size()) {
            indice++;
            // Lê a próxima linha candidata a completar a tag.
            String proxima = linhas.get(indice);
            // Outra tag iniciada em nova linha prova que a anterior ficou malformada.
            if (proxima.trim().startsWith("<")) {
                return new TagExtraction(trecho.toString(), linhaInicio, indiceInicial, -1);
            }

            // Procura o fim usando toda a nova linha, pois a tag já começou antes dela.
            fimTag = encontrarFimDaTag(proxima, -1);
            // Ao achar o fechamento, junta apenas o trecho até ele e devolve os novos limites.
            if (fimTag >= 0) {
                trecho.append('\n').append(proxima, 0, fimTag + 1);
                return new TagExtraction(
                        trecho.toString(),
                        linhaInicio,
                        indice,
                        fimTag + 1
                );
            }

            // Ainda não fechou: acrescenta a linha toda e tenta a seguinte.
            trecho.append('\n').append(proxima);
        }

        // Chegar ao fim do arquivo sem '>' produz uma extração marcada como inválida.
        return new TagExtraction(trecho.toString(), linhaInicio, indice, -1);
    }

    private ParsedTag interpretarTag(String trecho, int linha) {
        // Remove os delimitadores externos e espaços para analisar somente o conteúdo da tag.
        String interno = trecho.substring(1, trecho.length() - 1).trim();

        // Comentários HTML começam com !-- e não participam da estrutura de elementos.
        if (interno.startsWith("!--")) {
            // Um comentário só é ignorado se também terminar com --; caso contrário é um erro de sintaxe.
            return interno.endsWith("--") ? null : tagMalformada(trecho, linha);
        }

        // Trata DOCTYPE como declaração singleton, sem abertura pendente.
        if (interno.regionMatches(true, 0, "!doctype", 0, 8)) {
            // Aceita a declaração somente se não contiver delimitadores inesperados em seu conteúdo.
            return interno.matches("(?i)!doctype(?:\\s+[^<>]+)?")
                    ? new ParsedTag("!doctype", TagType.AUTOFECHAMENTO, linha, trecho)
                    : tagMalformada(trecho, linha);
        }

        // Uma barra inicial identifica uma tag de fechamento.
        boolean fechamento = interno.startsWith("/");
        // Para extrair o nome, remove a barra que apenas sinaliza o papel de fechamento.
        if (fechamento) {
            interno = interno.substring(1).trim();
        }

        // Uma barra antes do '>' identifica a forma explícita de tag autofechada.
        boolean autofechamento = interno.endsWith("/");
        // Para extrair o nome e atributos, remove a barra final de controle.
        if (autofechamento) {
            interno = interno.substring(0, interno.length() - 1).trim();
        }

        // Isola o primeiro identificador válido, descartando atributos.
        String nome = extrairNomeTag(interno);
        // Sem identificador, não existe uma tag HTML que possa ser validada.
        if (nome.isEmpty()) {
            return tagMalformada(trecho, linha);
        }

        // Separa o restante depois do nome para validar atributos ou conteúdo indevido.
        String restante = interno.substring(nome.length()).trim();
        // Fechamentos não podem conter atributos nem também se declarar autofechamento.
        if (fechamento && (!restante.isEmpty() || autofechamento)) {
            return tagMalformada(trecho, linha);
        }
        // Uma abertura não pode trazer um novo '<' dentro de seu próprio trecho.
        if (!fechamento && restante.contains("<")) {
            return tagMalformada(trecho, linha);
        }

        // A variável será preenchida pela regra estrutural apropriada a seguir.
        TagType tipo;
        // Fechamento tem prioridade porque a barra inicial já o caracteriza.
        if (fechamento) {
            tipo = TagType.FECHAMENTO;
        // Tags com barra final ou presentes na lista HTML de singletons não precisam ser fechadas.
        } else if (autofechamento || TagUtils.isSingleton(nome)) {
            tipo = TagType.AUTOFECHAMENTO;
        // Qualquer outro caso representa uma abertura comum.
        } else {
            tipo = TagType.ABERTURA;
        }

        // Retorna a representação completa que circulará pelas demais etapas da análise.
        return new ParsedTag(nome, tipo, linha, trecho);
    }

    // Localiza o '>' de fechamento, ignorando os que aparecem dentro de valores entre aspas.
    private int encontrarFimDaTag(String linha, int inicio) {
        // Armazena qual tipo de aspa está aberta; zero representa ausência de aspas abertas.
        char aspas = 0;
        // Se a tag começou nesta linha, pula o '<'; em continuação, inicia no primeiro caractere.
        int inicioBusca = inicio < 0 ? 0 : inicio + 1;
        // Examina cada caractere candidato a terminar a tag.
        for (int i = inicioBusca; i < linha.length(); i++) {
            // Lê o caractere atual uma única vez para as verificações seguintes.
            char caractere = linha.charAt(i);
            // Dentro de aspas, somente a mesma aspa pode encerrar o valor.
            if (aspas != 0) {
                // Ao encontrar a aspa correspondente, volta ao estado normal.
                if (caractere == aspas) {
                    aspas = 0;
                }
            // Fora de aspas, uma aspa simples ou dupla inicia um valor protegido.
            } else if (caractere == '\'' || caractere == '"') {
                aspas = caractere;
            // Fora de aspas, '>' conclui o trecho da tag.
            } else if (caractere == '>') {
                return i;
            }
        }
        // Nenhum delimitador de fim foi encontrado na linha examinada.
        return -1;
    }

    // Cria o mesmo marcador interno de erro usado quando uma tag não pôde ser compreendida.
    private ParsedTag tagMalformada(String trecho, int linha) {
        return new ParsedTag("?", TagType.ABERTURA, linha, trecho);
    }

    /**
     * Extrai apenas o nome da tag, descartando atributos.
     */
    private String extrairNomeTag(String conteudoTag) {
        // Uma string vazia não possui nome de tag.
        if (conteudoTag.isEmpty()) {
            return "";
        }

        // Marca o fim do trecho que pertence ao nome da tag.
        int fimNome = 0;
        // Aceita letras, números, ':' e '-' para cobrir nomes HTML e variações comuns.
        while (fimNome < conteudoTag.length()
                && (Character.isLetterOrDigit(conteudoTag.charAt(fimNome))
                || conteudoTag.charAt(fimNome) == ':'
                || conteudoTag.charAt(fimNome) == '-')) {
            fimNome++;
        }

        // Se o primeiro caractere não era permitido, não há nome válido a devolver.
        if (fimNome == 0) {
            return "";
        }

        // Retorna somente o identificador, deixando atributos fora da validação estrutural.
        return conteudoTag.substring(0, fimNome);
    }

}
