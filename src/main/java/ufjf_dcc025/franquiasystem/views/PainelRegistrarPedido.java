package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.controllers.PedidoController;
import ufjf_dcc025.franquiasystem.controllers.ProdutoController;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.models.Vendedor;
import ufjf_dcc025.franquiasystem.models.Franquia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PainelRegistrarPedido extends JPanel {
    private Usuario vendedor;
    private JComboBox<Produto> comboProdutos;
    private JSpinner spinnerQuantidade;
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloTabelaCarrinho;
    private List<Produto> produtosDisponiveis;
    private Map<Produto, Integer> carrinho;
    private JTextField campoNomeCliente;
    private JTextField campoFormaPagamento;
    private JTextField campoTaxas;
    private JTextField campoDescontos;
    private JComboBox<String> comboModalidade;

    private PainelMeusPedidos painelMeusPedidos;
    private Pedido pedidoEditando = null;

    public PainelRegistrarPedido(Usuario vendedor, PainelMeusPedidos painelMeusPedidos) {
        this(vendedor, painelMeusPedidos, null);
    }

    public PainelRegistrarPedido(Usuario vendedor, PainelMeusPedidos painelMeusPedidos, Pedido pedidoParaEditar) {
        this.vendedor = vendedor;
        this.painelMeusPedidos = painelMeusPedidos;
        this.carrinho = new HashMap<>();
        this.pedidoEditando = pedidoParaEditar;

        setLayout(new BorderLayout(10, 10));

        JPanel painelAdicionar = new JPanel(new FlowLayout());
        produtosDisponiveis = new ProdutoController().findAll();
        comboProdutos = new JComboBox<>(produtosDisponiveis.toArray(new Produto[0]));
        spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton btnAdicionar = new JButton("Adicionar ao Pedido");

        painelAdicionar.add(new JLabel("Produto:"));
        painelAdicionar.add(comboProdutos);
        painelAdicionar.add(new JLabel("Qtd:"));
        painelAdicionar.add(spinnerQuantidade);
        painelAdicionar.add(btnAdicionar);

        String[] colunas = {"Produto", "Qtd", "Preço Unit.", "Subtotal", "Ação"};
        modeloTabelaCarrinho = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tabelaCarrinho = new JTable(modeloTabelaCarrinho);
        tabelaCarrinho.getColumn("Ação").setCellRenderer(new ButtonRenderer());
        tabelaCarrinho.getColumn("Ação").setCellEditor(new ButtonEditorCarrinho(new JCheckBox()));

        JPanel painelFinalizar = new JPanel(new GridLayout(0, 2, 5, 5));
        campoNomeCliente = new JTextField();
        campoFormaPagamento = new JTextField();
        campoTaxas = new JTextField("0.0");
        campoDescontos = new JTextField("0.0");
        comboModalidade = new JComboBox<>(new String[]{"Retirada", "Entrega"});
        JButton btnFinalizarPedido = new JButton(pedidoParaEditar == null ? "Finalizar Pedido" : "Atualizar Pedido");

        painelFinalizar.add(new JLabel("Nome do Cliente:"));
        painelFinalizar.add(campoNomeCliente);
        painelFinalizar.add(new JLabel("Forma de Pagamento:"));
        painelFinalizar.add(campoFormaPagamento);
        painelFinalizar.add(new JLabel("Taxas:"));
        painelFinalizar.add(campoTaxas);
        painelFinalizar.add(new JLabel("Descontos:"));
        painelFinalizar.add(campoDescontos);
        painelFinalizar.add(new JLabel("Modalidade:"));
        painelFinalizar.add(comboModalidade);
        painelFinalizar.add(new JLabel());
        painelFinalizar.add(btnFinalizarPedido);

        add(painelAdicionar, BorderLayout.NORTH);
        add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        add(painelFinalizar, BorderLayout.SOUTH);

        if (pedidoEditando != null) {
            carregarDadosDoPedido(pedidoEditando);
        }

        btnAdicionar.addActionListener(e -> adicionarProdutoAoCarrinho());
        btnFinalizarPedido.addActionListener(e -> finalizarPedido());
    }

    private void carregarDadosDoPedido(Pedido pedido) {
        campoNomeCliente.setText(pedido.getNomeCliente());
        campoFormaPagamento.setText(pedido.getFormaPagamento());
        campoTaxas.setText(String.valueOf(pedido.getTaxas()));
        campoDescontos.setText(String.valueOf(pedido.getDescontos()));
        comboModalidade.setSelectedItem(pedido.getModalidadeEntrega());

        carrinho.clear();
        if (pedido.getProdutos() != null) {
            for (Map.Entry<Produto, Integer> entry : pedido.getProdutos().entrySet()) {
                Produto produtoDoPedido = entry.getKey();
                int quantidade = entry.getValue();

                Produto produtoDaLista = produtosDisponiveis.stream()
                        .filter(p -> p.getId() == produtoDoPedido.getId())
                        .findFirst()
                        .orElse(produtoDoPedido);

                carrinho.put(produtoDaLista, quantidade);
            }
        }
        atualizarTabelaCarrinho();
    }

    private void adicionarProdutoAoCarrinho() {
        Produto produtoSelecionado = (Produto) comboProdutos.getSelectedItem();
        int quantidade = (int) spinnerQuantidade.getValue();

        if (produtoSelecionado == null) return;

        int estoqueDisponivel = produtoSelecionado.getQuantidade();
        int quantidadeNoCarrinho = carrinho.getOrDefault(produtoSelecionado, 0);
        int quantidadeTotal = quantidadeNoCarrinho + quantidade;

        if (quantidadeTotal > estoqueDisponivel) {
            JOptionPane.showMessageDialog(this,
                    "Quantidade solicitada excede o estoque disponível (" + estoqueDisponivel + ").",
                    "Erro de estoque",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        carrinho.put(produtoSelecionado, quantidadeTotal);
        atualizarTabelaCarrinho();
    }

    private void atualizarTabelaCarrinho() {
        modeloTabelaCarrinho.setRowCount(0);
        for (Map.Entry<Produto, Integer> entry : carrinho.entrySet()) {
            Produto p = entry.getKey();
            int qtd = entry.getValue();
            double subtotal = p.getPreco() * qtd;
            modeloTabelaCarrinho.addRow(new Object[]{p.getNome(), qtd, p.getPreco(), subtotal, "X"});
        }
    }

    private void finalizarPedido() {
        String nomeCliente = campoNomeCliente.getText();
        String formaPagamento = campoFormaPagamento.getText();

        if (nomeCliente.isEmpty() || formaPagamento.isEmpty() || carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha os dados obrigatórios e adicione produtos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double taxas, descontos;
        try {
            taxas = Double.parseDouble(campoTaxas.getText().replace(',', '.'));
            descontos = Double.parseDouble(campoDescontos.getText().replace(',', '.'));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Taxas e descontos devem ser números válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String modalidade = (String) comboModalidade.getSelectedItem();
        PedidoController pedidoController = new PedidoController();

        if (pedidoEditando == null) {
            Vendedor vendedorLogado = (Vendedor) vendedor;
            Franquia franquia = vendedorLogado.getFranquia();
            Pedido novoPedido = new Pedido(nomeCliente, formaPagamento, new HashMap<>(carrinho), taxas, descontos, modalidade, vendedorLogado, franquia);
            pedidoController.create(novoPedido);
            JOptionPane.showMessageDialog(this, "Pedido criado com sucesso!");
            limparCampos();
        } else {
            pedidoEditando.setNomeCliente(nomeCliente);
            pedidoEditando.setFormaPagamento(formaPagamento);
            pedidoEditando.setProdutos(new HashMap<>(carrinho));
            pedidoEditando.setTaxas(taxas);
            pedidoEditando.setDescontos(descontos);
            pedidoEditando.setModalidadeEntrega(modalidade);
            pedidoController.update(pedidoEditando);
            JOptionPane.showMessageDialog(this, "Pedido atualizado com sucesso!");
        }

        if (painelMeusPedidos != null) {
            painelMeusPedidos.atualizarTabela();
        }
    }

    private void limparCampos() {
        campoNomeCliente.setText("");
        campoFormaPagamento.setText("");
        campoTaxas.setText("0.0");
        campoDescontos.setText("0.0");
        comboModalidade.setSelectedIndex(0);
        carrinho.clear();
        atualizarTabelaCarrinho();
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("X");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditorCarrinho extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        public ButtonEditorCarrinho(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("X");
            button.addActionListener(e -> {
                fireEditingStopped();

                String nomeProduto = (String) modeloTabelaCarrinho.getValueAt(selectedRow, 0);
                Produto produtoParaRemover = null;
                for (Produto p : carrinho.keySet()) {
                    if (p.getNome().equals(nomeProduto)) {
                        produtoParaRemover = p;
                        break;
                    }
                }
                if (produtoParaRemover != null) {
                    carrinho.remove(produtoParaRemover);
                    atualizarTabelaCarrinho();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "X";
        }
    }
}
