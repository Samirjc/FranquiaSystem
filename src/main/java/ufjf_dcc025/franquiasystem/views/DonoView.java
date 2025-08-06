package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.models.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DonoView extends JFrame {
    private final Usuario dono;
    private final CardLayout cardLayout;
    private final JPanel painelCentral;

    public DonoView(Usuario dono) {
        this.dono = dono;

        setTitle("Franquia System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TOPO
        JPanel painelTopo = new JPanel(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem itemFranquias = new JMenuItem("Franquias");
        itemFranquias.addActionListener(this::mostrarFranquias);

        JMenuItem itemGerentes = new JMenuItem("Gerentes");
        itemGerentes.addActionListener(this::mostrarGerentes);

        menu.add(itemFranquias);
        menu.add(itemGerentes);
        menuBar.add(menu);
        painelTopo.add(menuBar, BorderLayout.WEST);

        JLabel titulo = new JLabel("Franquia System", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        painelTopo.add(titulo, BorderLayout.CENTER);

        add(painelTopo, BorderLayout.NORTH);

        // PAINEL CENTRAL
        cardLayout = new CardLayout();
        painelCentral = new JPanel(cardLayout);

        painelCentral.add(criarBoasVindas(), "BOAS_VINDAS");
        painelCentral.add(new PainelFranquias(), "FRANQUIAS");
        painelCentral.add(new PainelGerentes(), "GERENTES");

        add(painelCentral, BorderLayout.CENTER);

        cardLayout.show(painelCentral, "BOAS_VINDAS");

        //Envia para tela do login
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dispose(); // Fecha a VendedorView
                new LoginView().setVisible(true); // Abre uma nova LoginView
            }
        });
    }

    private JPanel criarBoasVindas() {
        JPanel painel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Bem-vindo, Administrador " + dono.getNome(), SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        painel.add(label, BorderLayout.CENTER);
        return painel;
    }

    private void mostrarFranquias(ActionEvent e) {
        cardLayout.show(painelCentral, "FRANQUIAS");
    }

    private void mostrarGerentes(ActionEvent e) {
        cardLayout.show(painelCentral, "GERENTES");
    }
}
