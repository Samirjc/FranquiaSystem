package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.models.Usuario;
import javax.swing.*;
import java.awt.*;

public class VendedorView extends JFrame {
    private Usuario vendedor;

    public VendedorView(Usuario vendedorLogado) {
        this.vendedor = vendedorLogado;

        setTitle("Painel do Vendedor: " + vendedor.getNome());
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JPanel painelTopo = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Franquia System", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        painelTopo.add(titulo, BorderLayout.CENTER);
        add(painelTopo, BorderLayout.NORTH);



        // --- Adiciona a lógica de fechamento da janela ---
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dispose();
                new LoginView().setVisible(true);
            }
        });

        // --- Estrutura de Abas ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Cria o painel "Meus Pedidos" primeiro, pois ele precisa ser "avisado"
        PainelMeusPedidos painelMeusPedidos = new PainelMeusPedidos(this.vendedor);

        // 2. Cria o painel "Registrar Pedido" e passa a referência do outro painel para ele
        PainelRegistrarPedido painelRegistrarPedido = new PainelRegistrarPedido(this.vendedor, painelMeusPedidos);

        tabbedPane.addTab("Registrar Novo Pedido", painelRegistrarPedido);
        tabbedPane.addTab("Meus Pedidos", painelMeusPedidos);

        add(tabbedPane, BorderLayout.CENTER);
    }
}