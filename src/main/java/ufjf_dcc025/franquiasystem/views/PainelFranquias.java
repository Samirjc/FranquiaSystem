package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.FranquiaController;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.exceptions.ErroCriacaoFranquiaException;
import ufjf_dcc025.franquiasystem.dto.VendedorRankingDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelFranquias extends JPanel {
    private final DefaultTableModel modeloTabelaFranquias;
    private final JTable tabelaFranquias;
    private final List<Franquia> franquias;

    public PainelFranquias() {
        setLayout(new BorderLayout());

        FranquiaController franquiaController = new FranquiaController();
        franquias = franquiaController.findAll();

        JButton btnNovaFranquia = new JButton("Nova Franquia");
        btnNovaFranquia.addActionListener(e -> novaFranquiaDialog());
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelTopo.add(btnNovaFranquia);
        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Endereço", "Gerente"};
        modeloTabelaFranquias = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaFranquias = new JTable(modeloTabelaFranquias);
        tabelaFranquias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabelaFranquias), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEditar = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");
        JButton btnDetalhes = new JButton("Detalhes");

        painelBotoes.add(btnDetalhes);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        btnEditar.addActionListener(e -> editarFranquia());
        btnRemover.addActionListener(e -> removerFranquia());
        btnDetalhes.addActionListener(e -> verVendedoresDaFranquia());

        preencherTabelaFranquias();
    }

    private void preencherTabelaFranquias() {
        modeloTabelaFranquias.setRowCount(0);
        for (Franquia f : franquias) {
            modeloTabelaFranquias.addRow(new Object[]{
                    f.getId(),
                    f.getNome(),
                    f.getEndereco(),
                    f.getGerente() != null ? f.getGerente().getNome() : ""
            });
        }
    }

    private void editarFranquia() {
        int linha = tabelaFranquias.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma franquia para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Franquia franquia = franquias.get(linha);

        JTextField campoNome = new JTextField(franquia.getNome());
        JTextField campoEndereco = new JTextField(franquia.getEndereco());

        UsuarioController usuarioController = new UsuarioController();
        List<Usuario> gerentes = usuarioController.findAllGerentes();

        JComboBox<Usuario> comboGerentes = new JComboBox<>(gerentes.toArray(new Usuario[0]));
        if (franquia.getGerente() != null) {
            comboGerentes.setSelectedItem(franquia.getGerente());
        }

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("Endereço:"));
        painel.add(campoEndereco);
        painel.add(new JLabel("Gerente:"));
        painel.add(comboGerentes);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Editar Franquia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            String endereco = campoEndereco.getText().trim();
            Usuario gerente = (Usuario) comboGerentes.getSelectedItem();

            if (!nome.isEmpty() && !endereco.isEmpty() && gerente != null) {
                franquia.setNome(nome);
                franquia.setEndereco(endereco);
                franquia.setGerente(gerente);
                new FranquiaController().update(franquia);
                preencherTabelaFranquias();
            }
        }
    }

    private void removerFranquia() {
        int linha = tabelaFranquias.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma franquia para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Remover esta franquia?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Franquia f = franquias.get(linha);
            new FranquiaController().delete(f.getId());
            franquias.remove(linha);
            preencherTabelaFranquias();
        }
    }

    private void novaFranquiaDialog() {
        JTextField campoNome = new JTextField();
        JTextField campoEndereco = new JTextField();

        UsuarioController usuarioController = new UsuarioController();
        List<Usuario> gerentes = usuarioController.findAllGerentes();

        if (gerentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum gerente cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Usuario> comboGerentes = new JComboBox<>(gerentes.toArray(new Usuario[0]));

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome da Franquia:"));
        painel.add(campoNome);
        painel.add(new JLabel("Endereço:"));
        painel.add(campoEndereco);
        painel.add(new JLabel("Gerente:"));
        painel.add(comboGerentes);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Nova Franquia", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            String endereco = campoEndereco.getText().trim();
            Usuario gerente = (Usuario) comboGerentes.getSelectedItem();

            if (!nome.isEmpty() && !endereco.isEmpty() && gerente != null) {
                Franquia novaFranquia = new Franquia(nome, endereco, gerente);

                try {
                    int novaFranquiaId = new FranquiaController().create(novaFranquia).getId();
                    novaFranquia.setId(novaFranquiaId);
                } catch (ErroCriacaoFranquiaException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao criar nova franquia.", "Erro", JOptionPane.ERROR_MESSAGE);
                }

                franquias.add(novaFranquia);
                preencherTabelaFranquias();
            } else {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verVendedoresDaFranquia() {
        int linha = tabelaFranquias.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma franquia para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Franquia franquiaSelecionada = franquias.get(linha);
        List<VendedorRankingDTO> vendedores = new UsuarioController().findAllVendedoresByFranquiaId(franquiaSelecionada.getId(), "quantidade");

        if (vendedores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum vendedor encontrado para esta franquia.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] colunas = {"ID", "Nome", "Email"};
        Object[][] dados = new Object[vendedores.size()][colunas.length];

        for (int i = 0; i < vendedores.size(); i++) {
            VendedorRankingDTO v = vendedores.get(i);
            dados[i][0] = v.getId();
            dados[i][1] = v.getNome();
            dados[i][2] = v.getEmail();
        }

        JTable tabela = new JTable(dados, colunas);
        tabela.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        JLabel titulo = new JLabel("Vendedores", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, painel, "Vendedores da Franquia: " + franquiaSelecionada.getNome(), JOptionPane.PLAIN_MESSAGE);
    }
}
