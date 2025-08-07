package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.FranquiaController;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.dto.VendedorRankingDTO;
import ufjf_dcc025.franquiasystem.models.Franquia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class PainelRanking extends JPanel {
    private final UsuarioController usuarioController = new UsuarioController();
    private final FranquiaController franquiaController = new FranquiaController();

    private final JComboBox<Franquia> comboFranquias = new JComboBox<>();
    private final JComboBox<String> comboOrdenarPor = new JComboBox<>(new String[]{"valor", "quantidade"});
    private final DefaultTableModel tabelaModel;
    private final JTable tabela;

    public PainelRanking() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel painelSelecao = new JPanel();
        painelSelecao.setLayout(new FlowLayout(FlowLayout.LEFT));

        painelSelecao.add(new JLabel("Franquia:"));
        painelSelecao.add(comboFranquias);

        painelSelecao.add(Box.createHorizontalStrut(20));
        painelSelecao.add(new JLabel("Ordenar por:"));
        painelSelecao.add(comboOrdenarPor);

        add(painelSelecao, BorderLayout.NORTH);

        String[] colunas = {"#", "Nome", "Email", "Quantidade de Vendas", "Valor Total de Vendas"};
        tabelaModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(tabelaModel);
        tabela.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        carregarFranquias();

        comboFranquias.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                atualizarRanking();
            }
        });

        comboOrdenarPor.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                atualizarRanking();
            }
        });
    }

    private void carregarFranquias() {
        comboFranquias.removeAllItems();
        List<Franquia> franquias = franquiaController.findAll();

        for (Franquia f : franquias) {
            comboFranquias.addItem(f);
        }

        if (!franquias.isEmpty()) {
            comboFranquias.setSelectedIndex(0);
            atualizarRanking();
        }
    }

    private void atualizarRanking() {
        tabelaModel.setRowCount(0);

        Franquia franquiaSelecionada = (Franquia) comboFranquias.getSelectedItem();
        String ordenarPor = (String) comboOrdenarPor.getSelectedItem();

        if (franquiaSelecionada == null || ordenarPor == null) return;

        List<VendedorRankingDTO> ranking = usuarioController.findAllVendedoresByFranquiaId(
            franquiaSelecionada.getId(), ordenarPor
        );

        for (int i = 0; i < ranking.size(); i++) {
            VendedorRankingDTO v = ranking.get(i);
            Object[] row = {
                i + 1,
                v.getNome(),
                v.getEmail(),
                v.getQuantidadeVendas(),
                String.format("R$ %.2f", v.getValorTotalVendas())
            };
            tabelaModel.addRow(row);
        }
    }
}
