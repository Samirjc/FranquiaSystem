package ufjf_dcc025.franquiasystem.controllers;

import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.repositories.PedidosRepository;
import java.util.List;

public class PedidoController {
    private final PedidosRepository pedidosRepository;

    public PedidoController() {
        this.pedidosRepository = new PedidosRepository();
    }

    public List<Pedido> findAll() {
        //a lógica para filtrar por franquia deve entrar aqui.
        return pedidosRepository.findAll();
    }
    public void create(Pedido pedido) {
        // A única função dele é repassar o pedido para o repositório
        pedidosRepository.create(pedido);
    }
    //Aqui entrariam métodos de update, delete, aprovarPedido, etc.
}