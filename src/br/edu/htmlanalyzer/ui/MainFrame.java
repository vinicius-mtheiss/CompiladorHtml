package br.edu.htmlanalyzer.ui;

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
import javax.swing.table.DefaultTableModel;
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

    private final HtmlAnalyzerService analyzerService;
    private final JTextField campoArquivo;
    private final JLabel labelStatus;
    private final JTextArea areaRelatorio;
    private final JTextArea areaHierarquia;
    private final JTable tabelaEstatisticas;
    private final DefaultTableModel modeloTabela;

    public MainFrame() {
        super("Analisador de HTML - Validador Estrutural");
        this.analyzerService = new HtmlAnalyzerService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        campoArquivo = new JTextField(40);
        campoArquivo.setEditable(false);
        labelStatus = new JLabel("Nenhum arquivo analisado.");
        areaRelatorio = criarTextArea();
        areaHierarquia = criarTextArea();

        modeloTabela = new DefaultTableModel(
                new String[]{"Tag", "Frequência", "Tipo", "1ª Ocorrência"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEstatisticas = new JTable(modeloTabela);
        tabelaEstatisticas.setRowHeight(24);
        tabelaEstatisticas.getTableHeader().setReorderingAllowed(false);

        montarLayout();
    }

    private JTextArea criarTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void montarLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel painelSuperior = new JPanel(new BorderLayout(8, 8));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JPanel painelArquivo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        painelArquivo.add(new JLabel("Arquivo:"));
        painelArquivo.add(campoArquivo);

        JButton botaoSelecionar = new JButton("Selecionar Arquivo");
        botaoSelecionar.addActionListener(e -> selecionarArquivo());
        painelArquivo.add(botaoSelecionar);

        JButton botaoAnalisar = new JButton("Analisar HTML");
        botaoAnalisar.addActionListener(e -> analisarArquivo());
        painelArquivo.add(botaoAnalisar);

        painelSuperior.add(painelArquivo, BorderLayout.CENTER);

        labelStatus.setBorder(BorderFactory.createEmptyBorder(4, 4, 8, 4));
        labelStatus.setFont(labelStatus.getFont().deriveFont(Font.BOLD, 14f));
        painelSuperior.add(labelStatus, BorderLayout.SOUTH);

        add(painelSuperior, BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Relatório", new JScrollPane(areaRelatorio));
        abas.addTab("Estatísticas", new JScrollPane(tabelaEstatisticas));
        abas.addTab("Hierarquia", new JScrollPane(areaHierarquia));

        add(abas, BorderLayout.CENTER);
    }

    private void selecionarArquivo() {
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Selecionar arquivo HTML ou TXT");
        seletor.setFileFilter(new FileNameExtensionFilter(
                "Arquivos HTML/TXT", "html", "htm", "txt"));

        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = seletor.getSelectedFile();
            campoArquivo.setText(arquivo.getAbsolutePath());
        }
    }

    private void analisarArquivo() {
        String caminho = campoArquivo.getText().trim();
        if (caminho.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um arquivo antes de analisar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            AnalysisResult resultado = analyzerService.analisar(caminho);
            exibirResultado(resultado);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao analisar arquivo:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            labelStatus.setText("Falha na análise.");
            labelStatus.setForeground(Color.RED);
        }
    }

    private void exibirResultado(AnalysisResult resultado) {
        areaRelatorio.setText(resultado.gerarRelatorio());

        if (resultado.isValido()) {
            labelStatus.setText("Documento VÁLIDO - " + resultado.getTotalTags() + " tags encontradas.");
            labelStatus.setForeground(new Color(0, 128, 0));
            preencherEstatisticas(resultado);
            preencherHierarquia(resultado);
        } else {
            labelStatus.setText("Documento INVÁLIDO - " + resultado.getErros().size() + " erro(s) encontrado(s).");
            labelStatus.setForeground(Color.RED);
            limparEstatisticas();
            areaHierarquia.setText("A hierarquia só é exibida quando o documento HTML está correto.");
        }
    }

    private void preencherEstatisticas(AnalysisResult resultado) {
        limparEstatisticas();
        for (TagStatistics estatistica : resultado.getEstatisticas()) {
            modeloTabela.addRow(new Object[]{
                    estatistica.getTag(),
                    estatistica.getFrequencia(),
                    estatistica.getTipo().getDescricao(),
                    estatistica.getPrimeiraOcorrencia()
            });
        }
    }

    private void limparEstatisticas() {
        modeloTabela.setRowCount(0);
    }

    private void preencherHierarquia(AnalysisResult resultado) {
        if (resultado.getRaiz() != null) {
            areaHierarquia.setText(resultado.getRaiz().gerarHierarquia(0));
        } else {
            areaHierarquia.setText("Nenhuma hierarquia disponível.");
        }
    }
}
