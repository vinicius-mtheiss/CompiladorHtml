package br.edu.htmlanalyzer;

/**
 * SUMÁRIO DO ARQUIVO: é o ponto de início da aplicação e cria a MainFrame na
 * thread apropriada para interfaces Swing.
 * POR QUE ESTÁ SEPARADO: mantém a inicialização mínima, deixando toda a
 * construção e o comportamento visual concentrados na classe da janela.
 */

import br.edu.htmlanalyzer.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicação Analisador de HTML.
 */
public class Main {

    // Método chamado pela JVM para iniciar o programa.
    public static void main(String[] args) {
        // Agenda a criação da janela na Event Dispatch Thread, que é a thread segura do Swing.
        SwingUtilities.invokeLater(() -> {
            // Tenta usar a aparência nativa do sistema operacional.
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Se a aparência não estiver disponível, a aplicação continua com o padrão do Java.
            } catch (Exception ignored) {
                // Mantém o Look and Feel padrão em caso de falha.
            }
            // Cria a janela que contém todos os controles da aplicação.
            MainFrame frame = new MainFrame();
            // Torna a janela visível somente depois que ela estiver configurada.
            frame.setVisible(true);
        });
    }
}
