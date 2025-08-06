package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.ProdutoController;
import ufjf_dcc025.franquiasystem.models.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelGerenciarEstoque extends JPanel {
    private JTable tabelaEstoque;
    private DefaultTableModel modeloTabela;
    private ProdutoController produtoController;
    private List<Produto> produtos;

    public PainelGerenciarEstoque() {
        this.produtoController = new ProdutoController();
        setLayout(new BorderLayout());

        add(new JLabel("Gerenciamento de Estoque de Produtos", SwingConstants.CENTER), BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome do Produto", "Preço (R$)", "Quantidade"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEstoque = new JTable(modeloTabela);
        tabelaEstoque.setDefaultRenderer(Object.class, new EstoqueBaixoRenderer());

        add(new JScrollPane(tabelaEstoque), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnNovo = new JButton("Novo Produto");
        JButton btnEditar = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> novoProdutoDialog());
        btnEditar.addActionListener(e -> editarProdutoDialog());
        btnRemover.addActionListener(e -> removerProduto());

        carregarEstoque();
    }

    private void carregarEstoque() {
        this.produtos = produtoController.findAll();

        modeloTabela.setRowCount(0);
        for (Produto produto : produtos) {
            // MUDANÇA: Adicionado o getQuantidade() na linha da tabela
            modeloTabela.addRow(new Object[]{
                    produto.getId(),
                    produto.getNome(),
                    String.format("%.2f", produto.getPreco()),
                    produto.getQuantidade()
            });
        }
    }

    private void novoProdutoDialog() {
        JTextField campoNome = new JTextField();
        JTextField campoPreco = new JTextField();
        JTextField campoQuantidade = new JTextField(); // MUDANÇA: Adicionado campo de quantidade

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome do Produto:"));
        painel.add(campoNome);
        painel.add(new JLabel("Preço:"));
        painel.add(campoPreco);
        painel.add(new JLabel("Quantidade em Estoque:"));
        painel.add(campoQuantidade);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Cadastrar Novo Produto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nome = campoNome.getText();
                double preco = Double.parseDouble(campoPreco.getText().replace(",", "."));
                int quantidade = Integer.parseInt(campoQuantidade.getText()); // MUDANÇA: Lendo a quantidade

                if (nome.isEmpty()) { /* ... validação ... */ }


                Produto novoProduto = new Produto(nome, preco, quantidade);
                produtoController.create(novoProduto);
                carregarEstoque();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Preço ou Quantidade inválida. Use apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarProdutoDialog() {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada == -1) { /* ... aviso ... */ return; }

        Produto produtoSelecionado = produtos.get(linhaSelecionada);

        JTextField campoNome = new JTextField(produtoSelecionado.getNome());
        JTextField campoPreco = new JTextField(String.valueOf(produtoSelecionado.getPreco()));
        JTextField campoQuantidade = new JTextField(String.valueOf(produtoSelecionado.getQuantidade())); // MUDANÇA: Adicionado campo

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Nome do Produto:"));
        painel.add(campoNome);
        painel.add(new JLabel("Preço:"));
        painel.add(campoPreco);
        painel.add(new JLabel("Quantidade em Estoque:"));
        painel.add(campoQuantidade);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Editar Produto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                produtoSelecionado.setNome(campoNome.getText());
                produtoSelecionado.setPreco(Double.parseDouble(campoPreco.getText().replace(",", ".")));
                produtoSelecionado.setQuantidade(Integer.parseInt(campoQuantidade.getText())); // MUDANÇA: Atualizando a quantidade

                produtoController.update(produtoSelecionado);
                carregarEstoque();
            } catch (NumberFormatException ex) { /* ... erro ... */ }
        }
    }

    private void removerProduto() {
        // (Este método não precisa de alterações)
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada == -1) { /* ... */ return; }
        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este produto?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            produtoController.delete(produtos.get(linhaSelecionada).getId());
            carregarEstoque();
        }
    }
}

// Classe interna para colorir as linhas com estoque baixo
class EstoqueBaixoRenderer extends DefaultTableCellRenderer {
    private static final int LIMITE_ESTOQUE_BAIXO = 5;
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        try {
            int quantidade = Integer.parseInt(table.getModel().getValueAt(row, 3).toString());
            if (quantidade < LIMITE_ESTOQUE_BAIXO && !isSelected) {
                c.setBackground(new Color(255, 204, 204));
            } else {
                c.setBackground(table.getBackground());
            }
        } catch (Exception e) {
            c.setBackground(table.getBackground());
        }
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        } else {
            c.setForeground(table.getForeground());
        }
        return c;
    }
}