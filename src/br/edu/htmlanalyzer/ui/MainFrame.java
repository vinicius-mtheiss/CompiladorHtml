package br.edu.htmlanalyzer.ui;

/**
 * SUMÁRIO DO ARQUIVO: declara a janela Swing, os controles e a atualização
 * visual dos resultados da análise escolhida pelo usuário.
 * POR QUE ESTÁ SEPARADO: a interface só conversa com o serviço e apresenta
 * dados; ela não lê, interpreta nem valida o HTML por conta própria.
 */

import br.edu.htmlanalyzer.datastructure.Lista;
import br.edu.htmlanalyzer.model.AnalysisResult;
import br.edu.htmlanalyzer.model.TagStatistics;
import br.edu.htmlanalyzer.service.HtmlAnalyzerService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;

/**
 * Janela principal da aplicação com interface gráfica Swing.
 */
public class MainFrame extends JFrame {

    // Serviço que concentra a lógica de análise, mantendo a interface livre de regras HTML.
    private final HtmlAnalyzerService analyzerService;
    // Campo apenas para exibir o caminho do arquivo escolhido.
    private final JTextField campoArquivo;
    // Rótulo que informa visualmente se a análise foi válida ou falhou.
    private final JLabel labelStatus;
    // Área da aba que mostra o relatório textual completo.
    private final JTextArea areaRelatorio;
    // Área da aba que mostra a árvore HTML em formato indentado.
    private final JTextArea areaHierarquia;
    // Componente de tabela que apresenta uma linha para cada estatística de tag.
    private final JTable tabelaEstatisticas;
    // Modelo próprio que armazena os dados da tabela sem usar arrays ou coleções prontas.
    private final StatisticsTableModel modeloTabela;

    // Monta e configura todos os componentes visuais que pertencem à janela principal.
    public MainFrame() {
        // Define o título mostrado na barra superior da janela.
        super("Analisador de HTML - Validador Estrutural");
        // Cria o ponto de acesso da tela à lógica de negócio.
        this.analyzerService = new HtmlAnalyzerService();

        // Faz o processo encerrar quando a janela principal for fechada.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Estabelece um tamanho mínimo que mantém a leitura confortável.
        setMinimumSize(new Dimension(900, 650));
        // Centraliza a janela na tela ao usar null como componente de referência.
        setLocationRelativeTo(null);

        // Cria o campo que exibirá o caminho do arquivo selecionado.
        campoArquivo = new JTextField(40);
        // Evita edição manual para garantir que o caminho venha do seletor de arquivos.
        campoArquivo.setEditable(false);
        // Mostra o estado inicial antes de haver qualquer análise.
        labelStatus = new JLabel("Nenhum arquivo analisado.");
        // Cria áreas de texto padronizadas para relatório e hierarquia.
        areaRelatorio = criarTextArea();
        areaHierarquia = criarTextArea();

        // Define colunas e comportamento do modelo que alimenta a tabela.
        modeloTabela = new StatisticsTableModel();
        // Vincula o modelo recém-criado à tabela visual.
        tabelaEstatisticas = new JTable(modeloTabela);
        // Ajusta uma altura legível para cada linha da tabela.
        tabelaEstatisticas.setRowHeight(24);
        // Impede que cabeçalhos sejam arrastados, preservando a ordem lógica das colunas.
        tabelaEstatisticas.getTableHeader().setReorderingAllowed(false);

        // Distribui todos os componentes nos painéis e regiões da janela.
        montarLayout();
    }

    // Cria uma área de texto comum para as abas que exibem conteúdo apenas de leitura.
    private JTextArea criarTextArea() {
        // Instancia o componente de texto vazio.
        JTextArea area = new JTextArea();
        // Impede alterações manuais no relatório calculado pela aplicação.
        area.setEditable(false);
        // Usa fonte monoespaçada para manter alinhamentos do relatório e da hierarquia.
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        // Faz textos longos quebrarem linha em vez de exigirem rolagem horizontal.
        area.setLineWrap(true);
        // Prefere quebrar nos limites de palavras quando possível.
        area.setWrapStyleWord(true);
        // Retorna a área pronta para ser colocada em uma aba.
        return area;
    }

    // Monta a disposição visual: controles no topo, status abaixo e abas no centro.
    private void montarLayout() {
        // Escolhe BorderLayout com espaços entre as regiões principais.
        setLayout(new BorderLayout(10, 10));

        // Agrupa seletor de arquivo e status na região superior.
        JPanel painelSuperior = new JPanel(new BorderLayout(8, 8));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        // Agrupa o rótulo, campo e botões em uma linha alinhada à esquerda.
        JPanel painelArquivo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        painelArquivo.add(new JLabel("Arquivo:"));
        painelArquivo.add(campoArquivo);

        // Cria o botão que abrirá o diálogo do sistema para escolher um arquivo.
        JButton botaoSelecionar = new JButton("Selecionar Arquivo");
        // Associa o clique à ação de seleção, sem misturar o código do evento ao layout.
        botaoSelecionar.addActionListener(e -> selecionarArquivo());
        painelArquivo.add(botaoSelecionar);

        // Cria o botão que dispara a análise do caminho já escolhido.
        JButton botaoAnalisar = new JButton("Analisar HTML");
        // Associa o clique ao método que chama o serviço de análise.
        botaoAnalisar.addActionListener(e -> analisarArquivo());
        painelArquivo.add(botaoAnalisar);

        // Coloca a linha de controles no centro do painel superior.
        painelSuperior.add(painelArquivo, BorderLayout.CENTER);

        labelStatus.setBorder(BorderFactory.createEmptyBorder(4, 4, 8, 4));
        labelStatus.setFont(labelStatus.getFont().deriveFont(Font.BOLD, 14f));
        painelSuperior.add(labelStatus, BorderLayout.SOUTH);

        // Exibe o painel completo na região norte da janela.
        add(painelSuperior, BorderLayout.NORTH);

        // Organiza as três saídas possíveis sem sobrecarregar uma única tela.
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Relatório", new JScrollPane(areaRelatorio));
        abas.addTab("Estatísticas", new JScrollPane(tabelaEstatisticas));
        abas.addTab("Hierarquia", new JScrollPane(areaHierarquia));

        // Coloca as abas na parte central, que ocupa o espaço restante da janela.
        add(abas, BorderLayout.CENTER);
    }

    // Abre o seletor de arquivos e copia o caminho escolhido para o campo visual.
    private void selecionarArquivo() {
        // Cria um diálogo padrão do sistema para escolha de arquivo.
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Selecionar arquivo HTML ou TXT");
        seletor.setFileFilter(new FileNameExtensionFilter(
                "Arquivos HTML/TXT", "html", "htm", "txt"));

        // Só atualiza o campo se o usuário confirmou a escolha.
        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Obtém o arquivo selecionado no diálogo.
            File arquivo = seletor.getSelectedFile();
            // Mostra o caminho absoluto que será enviado ao serviço.
            campoArquivo.setText(arquivo.getAbsolutePath());
        }
    }

    private void analisarArquivo() {
        // Lê o caminho exibido e elimina espaços externos por segurança.
        String caminho = campoArquivo.getText().trim();
        // Não chama o serviço quando o usuário ainda não selecionou nenhum arquivo.
        if (caminho.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um arquivo antes de analisar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            // Interrompe o método para não tentar analisar uma string vazia.
            return;
        }

        // Isola erros de arquivo e análise para apresentar uma mensagem amigável ao usuário.
        try {
            // Executa o fluxo completo e recebe seu resultado consolidado.
            AnalysisResult resultado = analyzerService.analisar(caminho);
            // Atualiza cada área da tela com o resultado recebido.
            exibirResultado(resultado);
        // Captura qualquer falha e a transforma em aviso visual, sem derrubar a aplicação.
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao analisar arquivo:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            labelStatus.setText("Falha na análise.");
            labelStatus.setForeground(Color.RED);
        }
    }

    private void exibirResultado(AnalysisResult resultado) {
        // Atualiza o texto da primeira aba, válido ou inválido.
        areaRelatorio.setText(resultado.gerarRelatorio());

        // Para um resultado válido, exibe indicadores positivos e conteúdo detalhado.
        if (resultado.isValido()) {
            labelStatus.setText("Documento VÁLIDO - " + resultado.getTotalTags() + " tags encontradas.");
            labelStatus.setForeground(new Color(0, 128, 0));
            preencherEstatisticas(resultado);
            preencherHierarquia(resultado);
        // Para um resultado inválido, mostra o problema e limpa conteúdos que não seriam confiáveis.
        } else {
            labelStatus.setText("Documento INVÁLIDO - " + resultado.getErros().size() + " erro(s) encontrado(s).");
            labelStatus.setForeground(Color.RED);
            limparEstatisticas();
            areaHierarquia.setText("A hierarquia só é exibida quando o documento HTML está correto.");
        }
    }

    private void preencherEstatisticas(AnalysisResult resultado) {
        // Entrega uma cópia das estatísticas para o modelo próprio da tabela.
        modeloTabela.setEstatisticas(resultado.getEstatisticas());
    }

    private void limparEstatisticas() {
        // Remove todas as linhas do modelo próprio.
        modeloTabela.limpar();
    }

    private void preencherHierarquia(AnalysisResult resultado) {
        // Só pede o texto recursivo quando existe uma raiz a ser exibida.
        if (resultado.getRaiz() != null) {
            areaHierarquia.setText(resultado.getRaiz().gerarHierarquia(0));
        // Mostra uma mensagem clara para o caso sem elementos hierárquicos disponíveis.
        } else {
            areaHierarquia.setText("Nenhuma hierarquia disponível.");
        }
    }

    // Modelo de tabela escrito manualmente para evitar estruturas prontas.
    private static class StatisticsTableModel extends AbstractTableModel {

        // Lista própria com as linhas de estatísticas atualmente exibidas.
        private Lista<TagStatistics> estatisticas = new Lista<>();

        // Substitui as linhas existentes e avisa a JTable para redesenhar.
        void setEstatisticas(Lista<TagStatistics> novasEstatisticas) {
            estatisticas = new Lista<>(novasEstatisticas);
            fireTableDataChanged();
        }

        // Esvazia o modelo para uma nova análise ou resultado inválido.
        void limpar() {
            estatisticas.clear();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return estatisticas.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Tag";
                case 1:
                    return "Frequência";
                case 2:
                    return "Tipo";
                case 3:
                    return "1ª Ocorrência";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TagStatistics estatistica = estatisticas.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return estatistica.getTag();
                case 1:
                    return estatistica.getFrequencia();
                case 2:
                    return estatistica.getTipo().getDescricao();
                case 3:
                    return estatistica.getPrimeiraOcorrencia();
                default:
                    return "";
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Mantém a tabela somente para consulta, sem edição acidental pelo usuário.
            return false;
        }
    }
}
