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
        painelBotoes.add(btnVerDetalhes);
        add(painelBotoes, BorderLayout.SOUTH);

        btnVerDetalhes.addActionListener(e -> verDetalhesPedido());

        carregarPedidos();
    }

    private void carregarPedidos() {
        Optional<Franquia> franquiaOpt = getFranquiaDoGerenteLogado();

        if (franquiaOpt.isEmpty()) {
            // Se não encontrou a franquia, limpa a tabela e não faz mais nada.
            modeloTabela.setRowCount(0);
            return;
        }
        Franquia franquiaDoGerente = franquiaOpt.get();

        //Busca todos os pedidos do sistema.
        List<Pedido> todosOsPedidos = pedidoController.findAll();

        //Filtra a lista para manter apenas os pedidos da franquia correta.
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

        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Detalhes do Pedido #").append(pedidoSelecionado.getId()).append("\n\n");
        detalhes.append("Cliente: ").append(pedidoSelecionado.getNomeCliente()).append("\n");

        if (pedidoSelecionado.getVendedor() != null) {
            detalhes.append("Vendedor: ").append(pedidoSelecionado.getVendedor().getNome()).append("\n");
        }

        detalhes.append("Forma de Pagamento: ").append(pedidoSelecionado.getFormaPagamento()).append("\n");
        detalhes.append("Modalidade: ").append(pedidoSelecionado.getModalidadeEntrega()).append("\n\n");

        detalhes.append("--- Produtos ---\n");
        if (pedidoSelecionado.getProdutos() != null && !pedidoSelecionado.getProdutos().isEmpty()) {
            for (Map.Entry<Produto, Integer> entry : pedidoSelecionado.getProdutos().entrySet()) {
                Produto p = entry.getKey();
                Integer qtd = entry.getValue();
                detalhes.append(String.format("- %d x %s (R$ %.2f cada)\n", qtd, p.getNome(), p.getPreco()));
            }
        } else {
            detalhes.append("Nenhum produto associado a este pedido.\n");
        }

        detalhes.append("\nTaxas: R$ ").append(String.format("%.2f", pedidoSelecionado.getTaxas()));
        detalhes.append("\nDescontos: R$ ").append(String.format("%.2f", pedidoSelecionado.getDescontos()));
        detalhes.append("\n\nValor Total: R$ ").append(String.format("%.2f", calcularValorTotal(pedidoSelecionado)));

        JTextArea areaTexto = new JTextArea(detalhes.toString());
        areaTexto.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setPreferredSize(new Dimension(400, 300)); // Define um bom tamanho para a janela

        JOptionPane.showMessageDialog(this, scrollPane, "Detalhes do Pedido #" + pedidoSelecionado.getId(), JOptionPane.INFORMATION_MESSAGE);
    }
    private Optional<Franquia> getFranquiaDoGerenteLogado() {
        // 1. Pega o ID do gerente logado.
        int gerenteId = this.gerente.getId();

        // 2. Usa o método do FranquiaController para encontrar a franquia.
        FranquiaController franquiaController = new FranquiaController();
        Optional<Franquia> franquiaOpt = franquiaController.findFranquiaByGerenteId(gerenteId);

        // 3. Verifica se a franquia foi encontrada.
        if (franquiaOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erro: Não foi possível encontrar a franquia para este gerente.", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }

        return franquiaOpt;
    }
}