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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cria o painel de abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Cria os painéis para cada funcionalidade (começaremos com o de Vendedores)
        // Por enquanto, os outros serão painéis vazios como placeholders.
        JPanel painelVendedores = new PainelGerenciarVendedores(this.gerente); // Nosso próximo passo
        JPanel painelPedidos = new JPanel();
        JPanel painelEstoque = new PainelGerenciarEstoque();
        JPanel painelRelatorios = new JPanel();

        // Adiciona os painéis como abas
        tabbedPane.addTab("Gerenciar Vendedores", painelVendedores);
        tabbedPane.addTab("Controlar Pedidos", painelPedidos);
        tabbedPane.addTab("Administrar Estoque", painelEstoque);
        tabbedPane.addTab("Relatórios", painelRelatorios);

        // Adiciona o painel de abas ao Frame
        add(tabbedPane, BorderLayout.CENTER);
    }
}