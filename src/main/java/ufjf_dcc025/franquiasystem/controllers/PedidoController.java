package ufjf_dcc025.franquiasystem.controllers;

import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;
import ufjf_dcc025.franquiasystem.repositories.PedidosRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.repositories.ProdutoRepository;

public class PedidoController {
    private final PedidosRepository pedidosRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController() {
        this.pedidosRepository = new PedidosRepository();
        this.produtoRepository = new ProdutoRepository();
    }

    public List<Pedido> findAll() {
        //a lógica para filtrar por franquia deve entrar aqui.
        return pedidosRepository.findAll();
    }
    public void create(Pedido pedido) {
        // A única função dele é repassar o pedido para o repositório
        pedidosRepository.create(pedido);
        
        for(Map.Entry<Produto, Integer> entry : pedido.getProdutos().entrySet()) {
            Produto produto = entry.getKey();
            produto.setQuantidade(produto.getQuantidade()-entry.getValue());
            produtoRepository.update(produto);
        }
    }

    
    public Map<Franquia, Double> calcularFaturamentoPorFranquia() {
       
        List<Pedido> todosOsPedidos = pedidosRepository.findAll();

       
        Map<Franquia, Double> faturamentoMap = new HashMap<>();

        // Itera sobre cada pedido para calcular seu valor
        for (Pedido pedido : todosOsPedidos) {
            double valorDoPedido = 0.0;

            // para cada produto, faz +preço * quantidade)
            for (Map.Entry<Produto, Integer> entry : pedido.getProdutos().entrySet()) {
                Produto produto = entry.getKey();
                Integer quantidade = entry.getValue();
                valorDoPedido += produto.getPreco() * quantidade;
            }

            valorDoPedido += pedido.getTaxas();
            valorDoPedido -= pedido.getDescontos();

            // 4. Adiciona o valor do pedido ao total da sua respectiva franquia
            Franquia franquia = pedido.getFranquia();
            if (franquia != null) {
                // valor atual ou 0 
                faturamentoMap.put(franquia, faturamentoMap.getOrDefault(franquia, 0.0) + valorDoPedido);
            }
        }

        return faturamentoMap;
    }
    public Map<Franquia, Integer> contarPedidosPorFranquia() {
        List<Pedido> todosOsPedidos = pedidosRepository.findAll();
        Map<Franquia, Integer> contagemMap = new HashMap<>();

        for (Pedido pedido : todosOsPedidos) {
            Franquia franquia = pedido.getFranquia();

            if (franquia != null) {
                contagemMap.put(franquia, contagemMap.getOrDefault(franquia, 0) + 1);
            }
        }

        return contagemMap;
    }
    
    public void update(Pedido pedidoAtualizado) {
        Optional<Pedido> pedidoAntigoOpt = pedidosRepository.findById(pedidoAtualizado.getId());

        if (pedidoAntigoOpt.isPresent()) {
            Pedido pedidoAntigo = pedidoAntigoOpt.get();

            // Cria um mapa com os produtos antigos para facilitar a comparação
            Map<Produto, Integer> produtosAntigos = pedidoAntigo.getProdutos();
            Map<Produto, Integer> produtosNovos = pedidoAtualizado.getProdutos();

            // Para todos os produtos do pedido antigo
            for (Map.Entry<Produto, Integer> entry : produtosAntigos.entrySet()) {
                Produto produto = entry.getKey();
                Integer qtdAntiga = entry.getValue();
                Integer qtdNova = produtosNovos.getOrDefault(produto, 0);

                int diferenca = qtdAntiga - qtdNova;

                if (diferenca != 0) {
                    produto.setQuantidade(produto.getQuantidade() + diferenca);
                    produtoRepository.update(produto);
                }
            }

            // Agora trata os produtos novos que não existiam no pedido antigo
            for (Map.Entry<Produto, Integer> entry : produtosNovos.entrySet()) {
                Produto produto = entry.getKey();
                if (!produtosAntigos.containsKey(produto)) {
                    produto.setQuantidade(produto.getQuantidade() - entry.getValue());
                    produtoRepository.update(produto);
                }
            }

            // Atualiza o pedido no repositório
            pedidosRepository.update(pedidoAtualizado);
        } else {
            throw new IllegalArgumentException("Pedido não encontrado com ID: " + pedidoAtualizado.getId());
        }
    }
}
