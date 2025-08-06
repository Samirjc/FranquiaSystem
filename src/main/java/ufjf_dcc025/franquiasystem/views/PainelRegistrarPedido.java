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

    public PainelRegistrarPedido(Usuario vendedor, PainelMeusPedidos painelMeusPedidos) {
        this.vendedor = vendedor;
        this.painelMeusPedidos = painelMeusPedidos;
        this.carrinho = new HashMap<>();
        setLayout(new BorderLayout(10, 10));

        // --- PAINEL SUPERIOR: SELEÇÃO DE PRODUTOS ---
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

        // --- PAINEL CENTRAL: CARRINHO DE COMPRAS ---
        String[] colunas = {"Produto", "Qtd", "Preço Unit.", "Subtotal"};
        modeloTabelaCarrinho = new DefaultTableModel(colunas, 0);
        tabelaCarrinho = new JTable(modeloTabelaCarrinho);

        // --- PAINEL INFERIOR: DADOS DO CLIENTE E FINALIZAÇÃO ---
        JPanel painelFinalizar = new JPanel(new GridLayout(0, 2, 5, 5));
        campoNomeCliente = new JTextField();
        campoFormaPagamento = new JTextField();

        // --- NOVOS COMPONENTES INICIALIZADOS COM VALORES PADRÃO ---
        campoTaxas = new JTextField("0.0");
        campoDescontos = new JTextField("0.0");
        String[] modalidades = {"Retirada", "Entrega"};
        comboModalidade = new JComboBox<>(modalidades);


        JButton btnFinalizarPedido = new JButton("Finalizar Pedido");

        painelFinalizar.add(new JLabel("Nome do Cliente:"));
        painelFinalizar.add(campoNomeCliente);
        painelFinalizar.add(new JLabel("Forma de Pagamento:"));
        painelFinalizar.add(campoFormaPagamento);
        painelFinalizar.add(new JLabel("Taxas:")); // NOVO
        painelFinalizar.add(campoTaxas); // NOVO
        painelFinalizar.add(new JLabel("Descontos:")); // NOVO
        painelFinalizar.add(campoDescontos); // NOVO
        painelFinalizar.add(new JLabel("Modalidade:")); // NOVO
        painelFinalizar.add(comboModalidade); // NOVO
        painelFinalizar.add(new JLabel()); // Placeholder
        painelFinalizar.add(btnFinalizarPedido);

        add(painelAdicionar, BorderLayout.NORTH);
        add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        add(painelFinalizar, BorderLayout.SOUTH);

        // --- AÇÕES DOS BOTÕES ---
        btnAdicionar.addActionListener(e -> adicionarProdutoAoCarrinho());
        btnFinalizarPedido.addActionListener(e -> finalizarPedido());
    }

    private void adicionarProdutoAoCarrinho() {
        Produto produtoSelecionado = (Produto) comboProdutos.getSelectedItem();
        int quantidade = (int) spinnerQuantidade.getValue();

        if (produtoSelecionado == null) return;

        if (quantidade > produtoSelecionado.getQuantidade()) {
            JOptionPane.showMessageDialog(this, "Estoque insuficiente! Disponível: " + produtoSelecionado.getQuantidade(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        carrinho.put(produtoSelecionado, quantidade);
        atualizarTabelaCarrinho();
    }

    private void atualizarTabelaCarrinho() {
        modeloTabelaCarrinho.setRowCount(0);
        for (Map.Entry<Produto, Integer> entry : carrinho.entrySet()) {
            Produto p = entry.getKey();
            int qtd = entry.getValue();
            double subtotal = p.getPreco() * qtd;
            modeloTabelaCarrinho.addRow(new Object[]{p.getNome(), qtd, p.getPreco(), subtotal});
        }
    }

    private void finalizarPedido() {
        String nomeCliente = campoNomeCliente.getText();
        String formaPagamento = campoFormaPagamento.getText();

        if (nomeCliente.isEmpty() || formaPagamento.isEmpty() || carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o nome do cliente, forma de pagamento e adicione produtos ao pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double taxas, descontos;
        try {
            taxas = Double.parseDouble(campoTaxas.getText().replace(',', '.'));
            descontos = Double.parseDouble(campoDescontos.getText().replace(',', '.'));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Os campos 'Taxas' e 'Descontos' devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String modalidade = (String) comboModalidade.getSelectedItem();

        Vendedor vendedorLogado = (Vendedor) this.vendedor;
        Franquia franquiaDoVendedor = vendedorLogado.getFranquia();


        Pedido novoPedido = new Pedido(
                nomeCliente,
                formaPagamento,
                new HashMap<>(carrinho),
                taxas,
                descontos,
                modalidade,
                vendedorLogado,
                franquiaDoVendedor
        );

        new PedidoController().create(novoPedido);

        ProdutoController produtoController = new ProdutoController();
        for (Map.Entry<Produto, Integer> entry : carrinho.entrySet()) {
            Produto produtoVendido = entry.getKey();
            int quantidadeVendida = entry.getValue();
            produtoVendido.setQuantidade(produtoVendido.getQuantidade() - quantidadeVendida);
            produtoController.update(produtoVendido);
        }

        JOptionPane.showMessageDialog(this, "Pedido finalizado com sucesso!");

        carrinho.clear();
        atualizarTabelaCarrinho();
        campoNomeCliente.setText("");
        campoFormaPagamento.setText("");
        campoTaxas.setText("0.0");
        campoDescontos.setText("0.0");
        comboModalidade.setSelectedItem("Retirada");


        if (painelMeusPedidos != null) {
            painelMeusPedidos.atualizarTabela();
        }
    }
}