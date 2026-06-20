package br.edu.htmlanalyzer;

import br.edu.htmlanalyzer.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicação Analisador de HTML.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Mantém o Look and Feel padrão em caso de falha.
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
