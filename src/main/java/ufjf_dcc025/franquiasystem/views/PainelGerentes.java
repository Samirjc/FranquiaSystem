package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.models.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ufjf_dcc025.franquiasystem.models.Gerente;

public class PainelGerentes extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(PainelGerentes.class.getName());

    private final DefaultTableModel modeloTabelaGerentes;
    private final JTable tabelaGerentes;
    private final List<Usuario> gerentes;

    public PainelGerentes() {
        setLayout(new BorderLayout());

        UsuarioController usuarioController = new UsuarioController();
        gerentes = usuarioController.findAllGerentes();

        JButton btnNovo = new JButton("Novo Gerente");
        btnNovo.addActionListener(e -> novoGerenteDialog());
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelTopo.add(btnNovo);
        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "CPF", "E-mail"};
        modeloTabelaGerentes = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaGerentes = new JTable(modeloTabelaGerentes);
        tabelaGerentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabelaGerentes), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEditar = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        btnEditar.addActionListener(e -> editarGerente());
        btnRemover.addActionListener(e -> removerGerente());

        preencherTabelaGerentes();
    }

    private void preencherTabelaGerentes() {
        modeloTabelaGerentes.setRowCount(0);
        if (gerentes == null) return;
        for (Usuario u : gerentes) {
            modeloTabelaGerentes.addRow(new Object[]{
                    u.getId(),
                    u.getNome(),
                    u.getCpf(),
                    u.getEmail() != null ? u.getEmail() : ""
            });
        }
    }

    private void editarGerente() {
        int linha = tabelaGerentes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um gerente para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario gerente = gerentes.get(linha);

        JTextField campoNome = new JTextField(gerente.getNome());
        JTextField campoCpf = new JTextField(gerente.getCpf());
        JTextField campoEmail = new JTextField(gerente.getEmail() != null ? gerente.getEmail() : "");
        JPasswordField campoSenha = new JPasswordField(); // vazio = não alterar

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("CPF:"));
        painel.add(campoCpf);
        painel.add(new JLabel("E-mail:"));
        painel.add(campoEmail);
        painel.add(new JLabel("Nova senha (deixe em branco para manter):"));
        painel.add(campoSenha);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Editar Gerente", JOptionPane.OK_CANCEL_OPTION);
        if (opcao == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            String cpf = campoCpf.getText().trim();
            String email = campoEmail.getText().trim();
            String novaSenha = new String(campoSenha.getPassword()).trim();

            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            gerente.setNome(nome);
            gerente.setCpf(cpf);
            gerente.setEmail(email);
            if (!novaSenha.isEmpty()) {
                gerente.setSenha(novaSenha);
            }

            try {
                new UsuarioController().update(gerente);
                preencherTabelaGerentes();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erro ao atualizar gerente", ex);
                JOptionPane.showMessageDialog(this, "Erro ao atualizar gerente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerGerente() {
        int linha = tabelaGerentes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um gerente para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Remover este gerente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Usuario u = gerentes.get(linha);
            try {
                new UsuarioController().delete(u.getId());
                gerentes.remove(linha);
                preencherTabelaGerentes();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erro ao remover gerente", ex);
                JOptionPane.showMessageDialog(this, "Erro ao remover gerente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void novoGerenteDialog() {
        JTextField campoNome = new JTextField();
        JTextField campoCpf = new JTextField();
        JTextField campoEmail = new JTextField();
        JPasswordField campoSenha = new JPasswordField();

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("CPF:"));
        painel.add(campoCpf);
        painel.add(new JLabel("E-mail:"));
        painel.add(campoEmail);
        painel.add(new JLabel("Senha:"));
        painel.add(campoSenha);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Novo Gerente", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            String cpf = campoCpf.getText().trim();
            String email = campoEmail.getText().trim();
            String senha = new String(campoSenha.getPassword()).trim();

            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario novoGerente = new Gerente(nome, senha, cpf, email);
            try {
                Usuario criado = new UsuarioController().create(novoGerente);
                if (criado != null) {
                    novoGerente.setId(criado.getId());
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erro ao criar novo gerente", ex);
                JOptionPane.showMessageDialog(this, "Erro ao criar novo gerente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            gerentes.add(novoGerente);
            preencherTabelaGerentes();
        }
    }
}
