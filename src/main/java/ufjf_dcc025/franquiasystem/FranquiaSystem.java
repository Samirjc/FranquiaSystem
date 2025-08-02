package ufjf_dcc025.franquiasystem;

import java.util.List;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.repositories.PedidosRepository;

public class FranquiaSystem {

    public static void main(String[] args) {
        PedidosRepository repository = new PedidosRepository();
        
        List<Pedido> pedidos = repository.findAll();
        
        for(Pedido pedido : pedidos) {
            System.out.println(pedido.getId());
            System.out.println(pedido.getNomeCliente());
            System.out.println(pedido.getModalidadeEntrega());
        }
    }
}
