package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.PedidoController;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PainelControlarPedidos extends JPanel {
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;
    private PedidoController pedidoController;
    private List<Pedido> pedidos;

    public PainelControlarPedidos() {
        this.pedidoController = new PedidoController();
        setLayout(new BorderLayout());

        add(new JLabel("Controle de Pedidos da Unidade", SwingConstants.CENTER), BorderLayout.NORTH);

        String[] colunas = {"ID Pedido", "Cliente", "Vendedor", "Valor Total", "Forma Pagamento", "Modalidade"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPedidos = new JTable(modeloTabela);
        add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnVerDetalhes = new JButton("Ver Detalhes / Editar");
        painelBotoes.add(btnVerDetalhes);
        add(painelBotoes, BorderLayout.SOUTH);

        btnVerDetalhes.addActionListener(e -> verDetalhesPedido());

        carregarPedidos();
    }

    private void carregarPedidos() {
        this.pedidos = pedidoController.findAll();

        modeloTabela.setRowCount(0);
        for (Pedido pedido : pedidos) {
            double valorTotal = calcularValorTotal(pedido);
            modeloTabela.addRow(new Object[]{
                    pedido.getId(),
                    pedido.getNomeCliente(),
                    pedido.getVendedor() != null ? pedido.getVendedor().getNome() : "N/A",
                    String.format("R$ %.2f", valorTotal),
                    pedido.getFormaPagamento(),
                    pedido.getModalidadeEntrega()
            });
        }
    }

    private double calcularValorTotal(Pedido pedido) {
        double total = 0;
        if (pedido.getProdutos() != null) {
            for (Map.Entry<Produto, Integer> entry : pedido.getProdutos().entrySet()) {
                total += entry.getKey().getPreco() * entry.getValue();
            }
        }
        total += pedido.getTaxas();
        total -= pedido.getDescontos();
        return total;
    }

    private void verDetalhesPedido() {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido pedidoSelecionado = pedidos.get(linhaSelecionada);

        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Detalhes do Pedido #").append(pedidoSelecionado.getId()).append("\n\n");
        detalhes.append("Cliente: ").append(pedidoSelecionado.getNomeCliente()).append("\n");
        detalhes.append("Vendedor: ").append(pedidoSelecionado.getVendedor() != null ? pedidoSelecionado.getVendedor().getNome() : "N/A").append("\n\n");
        detalhes.append("--- Produtos ---\n");

        if(pedidoSelecionado.getProdutos() != null && !pedidoSelecionado.getProdutos().isEmpty()){
            for (Map.Entry<Produto, Integer> entry : pedidoSelecionado.getProdutos().entrySet()) {
                Produto p = entry.getKey();
                Integer qtd = entry.getValue();
                detalhes.append(String.format("- %d x %s (R$ %.2f cada)\n", qtd, p.getNome(), p.getPreco()));
            }
        } else {
            detalhes.append("Nenhum produto associado a este pedido.\n");
        }

        detalhes.append("\nValor Total: R$ ").append(String.format("%.2f", calcularValorTotal(pedidoSelecionado)));

        JOptionPane.showMessageDialog(this, new JTextArea(detalhes.toString()), "Detalhes do Pedido", JOptionPane.INFORMATION_MESSAGE);
    }
}