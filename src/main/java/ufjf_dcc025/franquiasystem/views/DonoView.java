package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.FranquiaController;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DonoView extends JFrame {
    private final Usuario dono;
    private final CardLayout cardLayout;
    private final JPanel painelCentral;
    private JTable tabelaFranquias;
    private DefaultTableModel modeloTabelaFranquias;
    private List<Franquia> franquias;

    public DonoView(Usuario dono) {
        this.dono = dono;

        setTitle("Franquia System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TOPO
        JPanel painelTopo = new JPanel(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem itemFranquias = new JMenuItem("Franquias");
        itemFranquias.addActionListener(this::mostrarFranquias);

        JMenuItem itemGerentes = new JMenuItem("Gerentes");
        itemGerentes.addActionListener(this::mostrarGerentes);

        menu.add(itemFranquias);
        menu.add(itemGerentes);
        menuBar.add(menu);
        painelTopo.add(menuBar, BorderLayout.WEST);

        JLabel titulo = new JLabel("Franquia System", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        painelTopo.add(titulo, BorderLayout.CENTER);

        JButton btnNovaFranquia = new JButton("Nova Franquia");
        btnNovaFranquia.addActionListener(e -> novaFranquiaDialog());
        painelTopo.add(btnNovaFranquia, BorderLayout.EAST);

        add(painelTopo, BorderLayout.NORTH);

        // PAINEL CENTRAL
        cardLayout = new CardLayout();
        painelCentral = new JPanel(cardLayout);

        painelCentral.add(criarBoasVindas(), "BOAS_VINDAS");
        painelCentral.add(criarPainelFranquias(), "FRANQUIAS");
        painelCentral.add(criarPainelGerentes(), "GERENTES");

        add(painelCentral, BorderLayout.CENTER);

        cardLayout.show(painelCentral, "BOAS_VINDAS");
    }

    private JPanel criarBoasVindas() {
        JPanel painel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Bem-vindo, Administrador " + dono.getNome(), SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        painel.add(label, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelFranquias() {
        FranquiaController franquiaController = new FranquiaController();
        franquias = franquiaController.findAll();

        JPanel painel = new JPanel(new BorderLayout());

        String[] colunas = {"ID", "Nome", "Endereço", "Gerente"};
        modeloTabelaFranquias = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaFranquias = new JTable(modeloTabelaFranquias);
        tabelaFranquias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabelaFranquias);

        preencherTabelaFranquias();

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEditar = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");

        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);

        btnEditar.addActionListener(e -> editarFranquia());
        btnRemover.addActionListener(e -> removerFranquia());

        painel.add(scrollPane, BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private void preencherTabelaFranquias() {
        modeloTabelaFranquias.setRowCount(0); // limpa
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

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Editar Franquia", JOptionPane.OK_CANCEL_OPTION);

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
                new FranquiaController().create(novaFranquia);

                franquias.add(novaFranquia);
                preencherTabelaFranquias();
            } else {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel criarPainelGerentes() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JTextArea("Conteúdo de gerentes aqui..."), BorderLayout.CENTER);
        return painel;
    }

    private void mostrarFranquias(ActionEvent e) {
        cardLayout.show(painelCentral, "FRANQUIAS");
    }

    private void mostrarGerentes(ActionEvent e) {
        cardLayout.show(painelCentral, "GERENTES");
    }
}
