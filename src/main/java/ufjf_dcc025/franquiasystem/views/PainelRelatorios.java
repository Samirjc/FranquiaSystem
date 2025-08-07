package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.FranquiaController;
import ufjf_dcc025.franquiasystem.controllers.PedidoController;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Gerente;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PainelRelatorios extends JPanel {
    private Gerente gerente;
    private PedidoController pedidoController;
    private FranquiaController franquiaController;

    public PainelRelatorios(Gerente gerente) {
        this.gerente = gerente;
        this.pedidoController = new PedidoController();
        this.franquiaController = new FranquiaController();

        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        setBorder(BorderFactory.createTitledBorder("Geração de Relatórios"));

        JButton btnHistoricoVendas = new JButton("Gerar Histórico de Vendas");
        JButton btnClientesRecorrentes = new JButton("Gerar Relatório de Clientes Recorrentes");

        add(btnHistoricoVendas);
        add(btnClientesRecorrentes);

        btnHistoricoVendas.addActionListener(e -> gerarHistoricoVendas());
        btnClientesRecorrentes.addActionListener(e -> gerarRelatorioClientes());
    }

    private void gerarHistoricoVendas() {
        Optional<Franquia> franquiaOpt = findFranquiaDoGerenteLogado();
        if (franquiaOpt.isEmpty()) return;
        Franquia franquiaDoGerente = franquiaOpt.get();

        List<Pedido> todosOsPedidos = pedidoController.findAll();
        List<Pedido> pedidosDaFranquia = todosOsPedidos.stream()
                .filter(p -> p.getFranquia() != null && p.getFranquia().getId() == franquiaDoGerente.getId())
                .collect(Collectors.toList());

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("--- Histórico de Vendas da Franquia: ").append(franquiaDoGerente.getNome()).append(" ---\n\n");


        double faturamentoTotal = 0.0;

        if (pedidosDaFranquia.isEmpty()) {
            relatorio.append("Nenhuma venda registrada para esta franquia.");
        } else {
            for (Pedido pedido : pedidosDaFranquia) {
                double valorDoPedido = calcularValorTotal(pedido); // Calcula o valor do pedido atual
                faturamentoTotal += valorDoPedido; // Soma o valor do pedido ao total

                relatorio.append(String.format("Pedido #%d | Cliente: %s | Vendedor: %s | Valor: R$ %.2f\n",
                        pedido.getId(),
                        pedido.getNomeCliente(),
                        pedido.getVendedor() != null ? pedido.getVendedor().getNome() : "N/A",
                        valorDoPedido
                ));
            }

            relatorio.append("\n----------------------------------------------------");
            relatorio.append(String.format("\n--- FATURAMENTO TOTAL: R$ %.2f ---", faturamentoTotal));
        }

        exibirRelatorio("Histórico de Vendas", relatorio.toString());
    }

    private void gerarRelatorioClientes() {
        Optional<Franquia> franquiaOpt = findFranquiaDoGerenteLogado();
        if (franquiaOpt.isEmpty()) return;
        Franquia franquiaDoGerente = franquiaOpt.get();

        List<Pedido> todosOsPedidos = pedidoController.findAll();
        List<Pedido> pedidosDaFranquia = todosOsPedidos.stream()
                .filter(p -> p.getFranquia() != null && p.getFranquia().getId() == franquiaDoGerente.getId())
                .collect(Collectors.toList());

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("--- Clientes Mais Recorrentes da Franquia: ").append(franquiaDoGerente.getNome()).append(" ---\n\n");

        if (pedidosDaFranquia.isEmpty()) {
            relatorio.append("Nenhum cliente registrado para esta franquia.");
        } else {
            Map<String, Integer> contagemClientes = new HashMap<>();
            for (Pedido pedido : pedidosDaFranquia) {
                String nomeCliente = pedido.getNomeCliente();
                contagemClientes.merge(nomeCliente, 1, Integer::sum);
            }

            List<Map.Entry<String, Integer>> listaClientes = new ArrayList<>(contagemClientes.entrySet());
            listaClientes.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            int rank = 1;
            for (Map.Entry<String, Integer> entry : listaClientes) {
                relatorio.append(String.format("%d. %s - %d compras\n",
                        rank++,
                        entry.getKey(),
                        entry.getValue()
                ));
            }
        }

        exibirRelatorio("Relatório de Clientes Recorrentes", relatorio.toString());
    }

    private Optional<Franquia> findFranquiaDoGerenteLogado() {
        int gerenteId = this.gerente.getId();
        Optional<Franquia> franquiaOpt = franquiaController.findFranquiaByGerenteId(gerenteId);
        if (franquiaOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erro: Não foi possível encontrar a franquia para este gerente.", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
        return franquiaOpt;
    }

    private void exibirRelatorio(String titulo, String conteudo) {
        JTextArea areaTexto = new JTextArea(conteudo);
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, titulo, JOptionPane.INFORMATION_MESSAGE);
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
}