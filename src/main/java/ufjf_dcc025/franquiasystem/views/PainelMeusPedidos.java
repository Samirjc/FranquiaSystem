package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.PedidoController;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;
import ufjf_dcc025.franquiasystem.models.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PainelMeusPedidos extends JPanel {
    private Usuario vendedorLogado;
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;
    private PedidoController pedidoController;
    private List<Pedido> meusPedidos;

    public PainelMeusPedidos(Usuario vendedor) {
        this.vendedorLogado = vendedor;
        this.pedidoController = new PedidoController();

        // Define o layout principal como BorderLayout
        setLayout(new BorderLayout(10, 10));

        // 1. Adiciona o título NO TOPO
        add(new JLabel("Pedidos Registrados por Você", SwingConstants.CENTER), BorderLayout.NORTH);

        // 2. Prepara a tabela de pedidos
        String[] colunas = {"ID Pedido", "Cliente", "Valor Total", "Forma Pagamento", "Modalidade"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPedidos = new JTable(modeloTabela);

        // 3.Adiciona a tabela (dentro de um painel de rolagem) NO CENTRO (CENTER)
        add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);

        // 4. Prepara o painel de botões
        JPanel painelBotoes = new JPanel();
        JButton btnVerDetalhes = new JButton("Ver Detalhes");
        painelBotoes.add(btnVerDetalhes);

        // 5. Adiciona os botões EMBAIXO (SOUTH)
        add(painelBotoes, BorderLayout.SOUTH);

        btnVerDetalhes.addActionListener(e -> verDetalhesPedido());
        carregarMeusPedidos();
    }

    private void carregarMeusPedidos() {
        List<Pedido> todosOsPedidos = pedidoController.findAll();
        this.meusPedidos = todosOsPedidos.stream()
                .filter(p -> p.getVendedor() != null && p.getVendedor().getId() == vendedorLogado.getId())
                .collect(Collectors.toList());

        modeloTabela.setRowCount(0);
        for (Pedido pedido : meusPedidos) {
            double valorTotal = calcularValorTotal(pedido);
            modeloTabela.addRow(new Object[]{
                    pedido.getId(),
                    pedido.getNomeCliente(),
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
        Pedido pedidoSelecionado = meusPedidos.get(linhaSelecionada);
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Detalhes do Pedido #").append(pedidoSelecionado.getId());
        //Código para montar a string de detalhes
        JOptionPane.showMessageDialog(this, new JTextArea(detalhes.toString()), "Detalhes do Pedido", JOptionPane.INFORMATION_MESSAGE);
    }
}