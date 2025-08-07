package ufjf_dcc025.franquiasystem.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.dto.VendedorRankingDTO;
import ufjf_dcc025.franquiasystem.exceptions.UsuarioNaoEncontradoException;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.models.Vendedor;
import ufjf_dcc025.franquiasystem.repositories.PedidosRepository;
import ufjf_dcc025.franquiasystem.repositories.UsuarioRepository;

public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioController() {
        this.usuarioRepository = new UsuarioRepository();
    }
    
    public Optional<Usuario> autenticar(String email, String senha) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        for(Usuario usuario : usuarios) {
            if(usuario.getEmail().equals(email) && usuario.getSenha().equals(senha)) {
                return Optional.of(usuario);
            }
        }
        
        return Optional.empty();
    }
    
    public Usuario findById(int id) throws UsuarioNaoEncontradoException {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        
        if(usuario.isPresent()) {
            return usuario.get();
        }else {
            throw new UsuarioNaoEncontradoException();
        }
    }
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public List<Usuario> findAllGerentes() {
        return usuarioRepository.findAll().stream().filter(u -> "gerente".equals(u.getTipo())).collect(Collectors.toList());
    }
    
    
    public List<VendedorRankingDTO> findAllVendedoresByFranquiaId(int franquiaId, String ordenarPor) {
        PedidosRepository pedidosRepository = new PedidosRepository();

        // Filtra todos os vendedores da franquia
        List<Vendedor> vendedores = usuarioRepository.findAll().stream()
            .filter(usuario -> usuario instanceof Vendedor)
            .map(usuario -> (Vendedor) usuario)
            .filter(v -> v.getFranquia() != null && v.getFranquia().getId() == franquiaId)
            .collect(Collectors.toList());

        // Busca todos os pedidos da franquia
        List<Pedido> pedidos = pedidosRepository.findAll().stream()
            .filter(p -> p.getFranquia() != null && p.getFranquia().getId() == franquiaId)
            .filter(p -> p.getVendedor() != null)
            .collect(Collectors.toList());

        // Agrupa pedidos por ID do vendedor
        Map<Integer, List<Pedido>> pedidosPorVendedorId = pedidos.stream()
            .collect(Collectors.groupingBy(p -> p.getVendedor().getId()));

        // Cria os DTOs
        List<VendedorRankingDTO> ranking = vendedores.stream()
            .map(vendedor -> {
                List<Pedido> pedidosDoVendedor = pedidosPorVendedorId.getOrDefault(vendedor.getId(), List.of());

                int quantidade = pedidosDoVendedor.size();
                double valorTotal = pedidosDoVendedor.stream()
                    .mapToDouble(this::calcularValorTotalPedido)
                    .sum();

                return new VendedorRankingDTO(
                    vendedor.getId(),
                    vendedor.getNome(),
                    vendedor.getEmail(),
                    quantidade,
                    valorTotal
                );
            })
            .collect(Collectors.toList());

        // Ordena com base no parâmetro
        if ("valor".equalsIgnoreCase(ordenarPor)) {
            ranking.sort((v1, v2) -> Double.compare(v2.getValorTotalVendas(), v1.getValorTotalVendas()));
        } else if ("quantidade".equalsIgnoreCase(ordenarPor)) {
            ranking.sort((v1, v2) -> Integer.compare(v2.getQuantidadeVendas(), v1.getQuantidadeVendas()));
        }

        return ranking;
    }


    
    public void delete(int id) {
        usuarioRepository.delete(id);
    }
    
    public void update(Usuario usuario) {
        usuarioRepository.update(usuario);
    }
    
    public Usuario create(Usuario usuario) {
        return usuarioRepository.create(usuario);
    }
    
    private double calcularValorTotalPedido(Pedido pedido) {
        double totalProdutos = pedido.getProdutos().entrySet().stream()
            .mapToDouble(entry -> entry.getKey().getPreco() * entry.getValue())
            .sum();

        return totalProdutos + pedido.getTaxas() - pedido.getDescontos();
    }
}
