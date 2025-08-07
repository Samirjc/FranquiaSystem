package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.models.Vendedor;

import ufjf_dcc025.franquiasystem.controllers.FranquiaController;
import ufjf_dcc025.franquiasystem.models.Franquia;
import java.util.Optional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PainelGerenciarVendedores extends JPanel {
    private Usuario gerente;
    private JTable tabelaVendedores;
    private DefaultTableModel modeloTabela;
    private UsuarioController usuarioController;
    private List<Usuario> vendedores;

    public PainelGerenciarVendedores(Usuario gerente) {
        this.gerente = gerente;
        this.usuarioController = new UsuarioController();
        setLayout(new BorderLayout());

        // Título do Painel
        add(new JLabel("Gestão da Equipe de Vendas", SwingConstants.CENTER), BorderLayout.NORTH);

        // Tabela para listar os vendedores
        String[] colunas = {"ID", "Nome", "CPF", "E-mail"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável diretamente
            }
        };
        tabelaVendedores = new JTable(modeloTabela);
        add(new JScrollPane(tabelaVendedores), BorderLayout.CENTER);

        // Painel com botões de ação
        JPanel painelBotoes = new JPanel();
        JButton btnNovo = new JButton("Novo Vendedor");
        JButton btnEditar = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ações dos botões
        btnNovo.addActionListener(e -> novoVendedorDialog());
        btnEditar.addActionListener(e -> editarVendedorDialog());
        btnRemover.addActionListener(e -> removerVendedor());

        // Carrega os dados na tabela
        carregarVendedores();
    }

    private void carregarVendedores() {
        // 1. Reutiliza o método que você criou para encontrar a franquia do gerente.
        Optional<Franquia> franquiaOpt = getFranquiaDoGerenteLogado();

        // Se não encontrou a franquia, simplesmente limpa a tabela e termina.
        if (franquiaOpt.isEmpty()) {
            modeloTabela.setRowCount(0);
            return;
        }
        Franquia franquiaDoGerente = franquiaOpt.get();

        // 2. Busca todos os usuários.
        List<Usuario> todosOsUsuarios = usuarioController.findAll();

        // 3. Filtra a lista para manter apenas os vendedores da franquia correta.
        this.vendedores = todosOsUsuarios.stream()
                .filter(u -> u instanceof Vendedor) // Garante que o usuário é um Vendedor
                .map(u -> (Vendedor) u) // Converte o tipo para Vendedor
                // O filtro principal: a franquia do vendedor não pode ser nula e seu ID deve ser igual ao do gerente
                .filter(v -> v.getFranquia() != null && v.getFranquia().getId() == franquiaDoGerente.getId())
                .collect(Collectors.toList());

        modeloTabela.setRowCount(0);
        for (Usuario vendedor : vendedores) {
            modeloTabela.addRow(new Object[]{
                    vendedor.getId(),
                    vendedor.getNome(),
                    vendedor.getCpf(),
                    vendedor.getEmail()
            });
        }
    }

    private void novoVendedorDialog() {
        JTextField campoNome = new JTextField();
        JTextField campoCpf = new JTextField();
        JTextField campoEmail = new JTextField();
        JPasswordField campoSenha = new JPasswordField();

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("CPF:"));
        painel.add(campoCpf);
        painel.add(new JLabel("E-mail (login):"));
        painel.add(campoEmail);
        painel.add(new JLabel("Senha:"));
        painel.add(campoSenha);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Cadastrar Novo Vendedor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText();
            String cpf = campoCpf.getText();
            String email = campoEmail.getText();
            String senha = new String(campoSenha.getPassword());


            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Chama nosso novo método para fazer todo o trabalho de busca e verificação
            Optional<Franquia> franquiaOpt = getFranquiaDoGerenteLogado();
            // Se o método não encontrou a franquia, ele já mostrou o erro, então só precisamos parar.
            if (franquiaOpt.isEmpty()) {
                return;
            }
            // Se encontrou, podemos usar o resultado com segurança.
            Franquia franquiaDoGerente = franquiaOpt.get();
            Usuario novoVendedor = new Vendedor(nome, senha, cpf, email, franquiaDoGerente);

            usuarioController.create(novoVendedor);
            carregarVendedores(); // Atualiza a tabela
        }
    }

    private void editarVendedorDialog() {
        int linhaSelecionada = tabelaVendedores.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario vendedorSelecionado = vendedores.get(linhaSelecionada);

        JTextField campoNome = new JTextField(vendedorSelecionado.getNome());
        JTextField campoCpf = new JTextField(vendedorSelecionado.getCpf());
        JTextField campoEmail = new JTextField(vendedorSelecionado.getEmail());
        JPasswordField campoSenha = new JPasswordField();
        campoSenha.setToolTipText("Deixe em branco para não alterar");

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("CPF:"));
        painel.add(campoCpf);
        painel.add(new JLabel("E-mail (login):"));
        painel.add(campoEmail);
        painel.add(new JLabel("Nova Senha:"));
        painel.add(campoSenha);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Editar Vendedor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            vendedorSelecionado.setNome(campoNome.getText());
            vendedorSelecionado.setCpf(campoCpf.getText());
            vendedorSelecionado.setEmail(campoEmail.getText());
            String novaSenha = new String(campoSenha.getPassword());
            if (!novaSenha.isEmpty()) {
                vendedorSelecionado.setSenha(novaSenha);
            }

            usuarioController.update(vendedorSelecionado);
            carregarVendedores(); // Atualiza a tabela
        }
    }

    private void removerVendedor() {
        int linhaSelecionada = tabelaVendedores.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um vendedor para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este vendedor?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            Usuario vendedorSelecionado = vendedores.get(linhaSelecionada);
            usuarioController.delete(vendedorSelecionado.getId());
            carregarVendedores(); // Atualiza a tabela
        }
    }

    private Optional<Franquia> getFranquiaDoGerenteLogado() {
        // 1. Pega o ID do gerente logado.
        int gerenteId = this.gerente.getId();

        // 2. Usa o novo método do FranquiaController para encontrar a franquia deste gerente.
        FranquiaController franquiaController = new FranquiaController();
        Optional<Franquia> franquiaOpt = franquiaController.findFranquiaByGerenteId(gerenteId);

        // 3. Verifica se a franquia foi encontrada.
        if (franquiaOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erro: Não foi possível encontrar a franquia para este gerente.", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }

        return franquiaOpt;
    }
}