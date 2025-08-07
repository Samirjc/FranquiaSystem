package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.FranquiaController;
import ufjf_dcc025.franquiasystem.controllers.PedidoController;
import ufjf_dcc025.franquiasystem.models.Franquia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;


public class PainelDesempenho extends JPanel {
    private final DefaultTableModel modeloTabelaFranquias;
    private final JTable tabelaFranquias;
    private final Map<Franquia, Double> faturamentoMap;
    private final Map<Franquia, Integer> contadorMap;
    private PedidoController pedidoController;
    private FranquiaController franquiaController;
    private final List<Franquia> franquias;

    public PainelDesempenho() {
        this.pedidoController = new PedidoController();
        this.franquiaController = new FranquiaController();
        franquias = franquiaController.findAll();
        faturamentoMap = pedidoController.calcularFaturamentoPorFranquia();
        contadorMap = pedidoController.contarPedidosPorFranquia();

        setLayout(new BorderLayout());

        String[] colunas = {"ID da Franquia", "Faturamento", "Nº de Pedidos", "Ticket Médio"};

        modeloTabelaFranquias = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaFranquias = new JTable(modeloTabelaFranquias);
        JScrollPane scrollPane = new JScrollPane(tabelaFranquias);

        add(scrollPane, BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        
            for (Franquia franquia : franquias) {
            int id = franquia.getId();
            double faturamento = faturamentoMap.getOrDefault(franquia, 0.0);
            int numeroPedidos = contadorMap.getOrDefault(franquia, 1);
            double ticketMedio = 0;
            if(numeroPedidos != 0) 
            ticketMedio = faturamento/numeroPedidos;

              modeloTabelaFranquias.addRow(new Object[]{
                id,
                faturamento,
                numeroPedidos,
                ticketMedio
            });
        }
        
    }
}
