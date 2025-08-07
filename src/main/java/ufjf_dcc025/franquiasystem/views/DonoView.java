package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.models.Usuario;

import javax.swing.*;
import java.awt.*;

public class DonoView extends JFrame {
    private final Usuario dono;

    public DonoView(Usuario dono) {
        this.dono = dono;

        setTitle("Painel do Dono: " + dono.getNome());
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TOPO
        JPanel painelTopo = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Franquia System", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        painelTopo.add(titulo, BorderLayout.CENTER);
        add(painelTopo, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel painelFranquias = new PainelFranquias();
        JPanel painelGerentes = new PainelGerentes();
        JPanel painelDesempenho = new PainelDesempenho();
        JPanel painelRanking = new PainelRanking();

        tabbedPane.addTab("Franquias", painelFranquias);
        tabbedPane.addTab("Gerentes", painelGerentes);
        tabbedPane.addTab("Desempenho", painelDesempenho);
        tabbedPane.addTab("Ranking", painelRanking);

        add(tabbedPane, BorderLayout.CENTER);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dispose();
                new LoginView().setVisible(true);
            }
        });
    }
}
