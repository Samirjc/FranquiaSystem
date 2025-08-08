package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.PedidoController;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;
import ufjf_dcc025.franquiasystem.models.Gerente;
import ufjf_dcc025.franquiasystem.controllers.FranquiaController;
import ufjf_dcc025.franquiasystem.models.Franquia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PainelControlarPedidos extends JPanel {
    private Gerente gerente;
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;
    private PedidoController pedidoController;
    private List<Pedido> pedidos;

    public PainelControlarPedidos(Gerente gerente) {
        this.gerente = gerente;
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
        JButton btnDeletar = new JButton("Deletar");

        painelBotoes.add(btnVerDetalhes);
        painelBotoes.add(btnDeletar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnVerDetalhes.addActionListener(e -> verDetalhesPedido());
        btnDeletar.addActionListener(e -> deletarPedido());

        carregarPedidos();
    }

    private void carregarPedidos() {
        Optional<Franquia> franquiaOpt = getFranquiaDoGerenteLogado();

        if (franquiaOpt.isEmpty()) {
            modeloTabela.setRowCount(0);
            return;
        }
        Franquia franquiaDoGerente = franquiaOpt.get();

        List<Pedido> todosOsPedidos = pedidoController.findAll();

        this.pedidos = todosOsPedidos.stream()
                .filter(p -> p.getFranquia() != null && p.getFranquia().getId() == franquiaDoGerente.getId())
                .collect(Collectors.toList());

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

        PainelRegistrarPedido painelEditar = new PainelRegistrarPedido(gerente, null, pedidoSelecionado);

        JPanel painelComMargem = new JPanel(new BorderLayout());
        painelComMargem.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painelComMargem.add(painelEditar, BorderLayout.CENTER);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Editar Pedido", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(painelComMargem);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        carregarPedidos();
    }

    private void deletarPedido() {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido pedidoSelecionado = pedidos.get(linhaSelecionada);

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja deletar o pedido ID " + pedidoSelecionado.getId() + "?",
                "Confirmar Deleção",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                pedidoController.delete(pedidoSelecionado.getId());
                JOptionPane.showMessageDialog(this, "Pedido deletado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar o pedido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Optional<Franquia> getFranquiaDoGerenteLogado() {
        int gerenteId = this.gerente.getId();

        FranquiaController franquiaController = new FranquiaController();
        Optional<Franquia> franquiaOpt = franquiaController.findFranquiaByGerenteId(gerenteId);

        if (franquiaOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erro: Não foi possível encontrar a franquia para este gerente.", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }

        return franquiaOpt;
    }
}
