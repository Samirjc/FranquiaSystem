package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.models.Usuario;
import javax.swing.*;
import java.awt.*;

public class GerenteView extends JFrame {
    private Usuario gerente;

    public GerenteView(Usuario gerenteLogado) {
        this.gerente = gerenteLogado;

        setTitle("Painel do Gerente: " + gerente.getNome());
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JPanel painelTopo = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Franquia System", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        painelTopo.add(titulo, BorderLayout.CENTER);
        add(painelTopo, BorderLayout.NORTH);


        // Cria o painel de abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Cria os painéis para cada funcionalidade
        JPanel painelVendedores = new PainelGerenciarVendedores(this.gerente);
        JPanel painelPedidos = new PainelControlarPedidos();
        JPanel painelEstoque = new PainelGerenciarEstoque();
        JPanel painelRelatorios = new JPanel();

        // Adiciona os painéis como abas
        tabbedPane.addTab("Gerenciar Vendedores", painelVendedores);
        tabbedPane.addTab("Controlar Pedidos", painelPedidos);
        tabbedPane.addTab("Administrar Estoque", painelEstoque);
        tabbedPane.addTab("Relatórios", painelRelatorios);

        // Adiciona o painel de abas ao Frame
        add(tabbedPane, BorderLayout.CENTER);


        //Envia para tela do login
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dispose(); // Fecha a VendedorView
                new LoginView().setVisible(true); // Abre uma nova LoginView
            }
        });
    }
}