package ufjf_dcc025.franquiasystem.repositories;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.models.Vendedor;

public class PedidosRepository extends ArquivoRepository{
    private static final String DIRETORIO = "data";
    private static final String CAMINHO_CSV = DIRETORIO + File.separator + "pedidos.csv";
    private static final String CAMINHO_CSV_RELACIONAMENTO = DIRETORIO + File.separator + "pedidos_produtos.csv";

    public void create(Pedido pedido) {
        try {
            File pasta = new File(DIRETORIO);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(CAMINHO_CSV);
            boolean escreverCabecalho = !arquivo.exists() || arquivo.length() == 0;
            int pedidoId = obterProximoId(arquivo);

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, true))) {
                if (escreverCabecalho) {
                    String[] cabecalho = {"id", "nomeCliente", "formaPagamento", "taxas", "descontos", "modalidadeEntrega", "vendedorId", "franquiaId"};
                    writer.writeNext(cabecalho);
                }
                String[] dados = {
                    Integer.toString(pedidoId),
                    pedido.getNomeCliente(),
                    pedido.getFormaPagamento(),
                    Double.toString(pedido.getTaxas()),
                    Double.toString(pedido.getDescontos()),
                    pedido.getModalidadeEntrega(),
                    Integer.toString(pedido.getVendedor().getId()),
                    Integer.toString(pedido.getFranquia().getId())
                };

                writer.writeNext(dados);
            }
            
            File arquivoRelacionamento = new File(CAMINHO_CSV_RELACIONAMENTO);
            boolean escreverCabecalhoRelacionamento = !arquivoRelacionamento.exists() || arquivoRelacionamento.length() == 0;
            
            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivoRelacionamento, true))) {
                if (escreverCabecalhoRelacionamento) {
                    String[] cabecalho = {"PedidoId", "ProdutoId", "quantidade"};
                    writer.writeNext(cabecalho);
                }
                
                for(Map.Entry<Produto, Integer> entry : pedido.getProdutos().entrySet()) {
                    String[] dados = {
                        Integer.toString(pedidoId),
                        Integer.toString(entry.getKey().getId()),
                        Integer.toString(entry.getValue())
                    };
                    writer.writeNext(dados);
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao salvar pedido no CSV: " + e.getMessage());
        }
    }
    
    public Optional<Pedido> findById(int id) {
        ProdutoRepository produtoRepository = new ProdutoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        FranquiaRepository franquiaRepository = new FranquiaRepository();
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) return Optional.empty();

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (linha.length >= 6 && Integer.parseInt(linha[0]) == id) {
                    String nome = linha[1];
                    String formaPagamento = linha[2];
                    double taxas = Double.parseDouble(linha[3]);
                    double descontos = Double.parseDouble(linha[4]);
                    String modalidadeEntrega = linha[5];
                    int vendedorId = Integer.parseInt(linha[6]);
                    int franquiaId = Integer.parseInt(linha[7]);

                    Map<Produto, Integer> produtos = buscarProdutosDoPedido(id, produtoRepository);
                    
                    Optional<Usuario> vendedorOpt = usuarioRepository.findById(vendedorId);
                    Optional<Franquia> franquiaOpt = franquiaRepository.findById(franquiaId);
                    Vendedor vendedor = null;
                    Franquia franquia = null;
                    
                    if(vendedorOpt.isPresent()) {
                        vendedor = (Vendedor)vendedorOpt.get();
                    }
                    if(franquiaOpt.isPresent()) {
                        franquia = franquiaOpt.get();
                    }
                    return Optional.of(new Pedido(id, nome, formaPagamento, produtos, taxas, descontos, modalidadeEntrega, vendedor, franquia));
                    
                }
            }

        } catch (IOException | CsvException e) {
            System.err.println("Erro ao buscar pedido por ID: " + e.getMessage());
        }

        return Optional.empty();
    }
    
    public List<Pedido> findAll() {
        ProdutoRepository produtoRepository = new ProdutoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        FranquiaRepository franquiaRepository = new FranquiaRepository();
        List<Pedido> pedidos = new ArrayList<>();
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) return pedidos;

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (linha.length >= 6) {
                    int id = Integer.parseInt(linha[0]);
                    String nome = linha[1];
                    String formaPagamento = linha[2];
                    double taxas = Double.parseDouble(linha[3]);
                    double descontos = Double.parseDouble(linha[4]);
                    String modalidadeEntrega = linha[5];
                    int vendedorId = Integer.parseInt(linha[6]);
                    int franquiaId = Integer.parseInt(linha[7]);
                    

                    Map<Produto, Integer> produtos = buscarProdutosDoPedido(id, produtoRepository);
                    Optional<Usuario> vendedorOpt = usuarioRepository.findById(vendedorId);
                    Optional<Franquia> franquiaOpt = franquiaRepository.findById(franquiaId);
                    Vendedor vendedor = null;
                    Franquia franquia = null;
                    
                    if(vendedorOpt.isPresent()) {
                        vendedor = (Vendedor)vendedorOpt.get();
                    }
                    if(franquiaOpt.isPresent()) {
                        franquia = franquiaOpt.get();
                    }
                    pedidos.add(new Pedido(id, nome, formaPagamento, produtos, taxas, descontos, modalidadeEntrega, vendedor, franquia));
                }
            }

        } catch (IOException | CsvException e) {
            System.err.println("Erro ao buscar todos os pedidos: " + e.getMessage());
        }

        return pedidos;
    }
    
    public void update(Pedido pedidoAtualizado) {
        File arquivo = new File(CAMINHO_CSV);
        File arquivoRelacionamento = new File(CAMINHO_CSV_RELACIONAMENTO);

        if (!arquivo.exists()) return;

        try {
            List<String[]> linhas = new CSVReader(new FileReader(arquivo)).readAll();
            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo))) {
                for (String[] linha : linhas) {
                    if (linha[0].equals("id")) {
                        writer.writeNext(linha);
                    } else if (Integer.parseInt(linha[0]) == pedidoAtualizado.getId()) {
                        String[] novaLinha = {
                            Integer.toString(pedidoAtualizado.getId()),
                            pedidoAtualizado.getNomeCliente(),
                            pedidoAtualizado.getFormaPagamento(),
                            Double.toString(pedidoAtualizado.getTaxas()),
                            Double.toString(pedidoAtualizado.getDescontos()),
                            pedidoAtualizado.getModalidadeEntrega(),
                            linha[6],
                            linha[7]
                        };
                        writer.writeNext(novaLinha);
                    } else {
                        writer.writeNext(linha);
                    }
                }
            }

            if (arquivoRelacionamento.exists()) {
                List<String[]> linhasRelacionamento = new CSVReader(new FileReader(arquivoRelacionamento)).readAll();
                try (CSVWriter writerRelacionamento = new CSVWriter(new FileWriter(arquivoRelacionamento))) {
                    for (String[] linha : linhasRelacionamento) {
                        if (linha[0].equals("PedidoId") || Integer.parseInt(linha[0]) != pedidoAtualizado.getId()) {
                            writerRelacionamento.writeNext(linha);
                        }
                    }

                    for (Map.Entry<Produto, Integer> entry : pedidoAtualizado.getProdutos().entrySet()) {
                        String[] novaRelacao = {
                            Integer.toString(pedidoAtualizado.getId()),
                            Integer.toString(entry.getKey().getId()),
                            Integer.toString(entry.getValue())
                        };
                        writerRelacionamento.writeNext(novaRelacao);
                    }
                }
            }

        } catch (IOException | CsvException e) {
            System.err.println("Erro ao atualizar o pedido: " + e.getMessage());
        }
    }
    
    public void delete(int pedidoId) {
        File arquivo = new File(CAMINHO_CSV);
        File arquivoRelacionamento = new File(CAMINHO_CSV_RELACIONAMENTO);

        if (!arquivo.exists()) return;

        try {
            List<String[]> linhas = new CSVReader(new FileReader(arquivo)).readAll();
            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo))) {
                for (String[] linha : linhas) {
                    if (linha[0].equals("id") || Integer.parseInt(linha[0]) != pedidoId) {
                        writer.writeNext(linha);
                    }
                }
            }
            
            if (arquivoRelacionamento.exists()) {
                List<String[]> linhasRelacionamento = new CSVReader(new FileReader(arquivoRelacionamento)).readAll();
                try (CSVWriter writerRelacionamento = new CSVWriter(new FileWriter(arquivoRelacionamento))) {
                    for (String[] linha : linhasRelacionamento) {
                        if (linha[0].equals("PedidoId") || Integer.parseInt(linha[0]) != pedidoId) {
                            writerRelacionamento.writeNext(linha);
                        }
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Erro ao deletar pedido: " + e.getMessage());
        }
    }
    
    private Map<Produto, Integer> buscarProdutosDoPedido(int pedidoId, ProdutoRepository produtoRepository) throws IOException, CsvException {
        Map<Produto, Integer> produtos = new HashMap<>();
        File arquivoRelacionamento = new File(CAMINHO_CSV_RELACIONAMENTO);
        if (!arquivoRelacionamento.exists()) return produtos;

        try (CSVReader reader = new CSVReader(new FileReader(arquivoRelacionamento))) {
            List<String[]> linhas = reader.readAll();
            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (Integer.parseInt(linha[0]) == pedidoId) {
                    int produtoId = Integer.parseInt(linha[1]);
                    
                    Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
                    if(produtoOpt.isPresent()) {
                        produtos.put(produtoOpt.get(), Integer.valueOf(linha[2]));
                    }
                }
            }
        }
        
        return produtos;
    }
}
