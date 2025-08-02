package ufjf_dcc025.franquiasystem.repositories;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.models.Pedido;
import ufjf_dcc025.franquiasystem.models.Produto;
import ufjf_dcc025.franquiasystem.models.Usuario;

public class PedidosRepository {
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
                    String[] cabecalho = {"id", "Nome", "Endereco", "gerenteId"};
                    writer.writeNext(cabecalho);
                }
                String[] dados = {
                    Integer.toString(pedidoId),
                    pedido.getNomeCliente(),
                    pedido.getFormaPagamento(),
                    Double.toString(pedido.getTaxas()),
                    Double.toString(pedido.getDescontos()),
                    pedido.getModalidadeEntrega()
                };

                writer.writeNext(dados);
            }
            
            File arquivoRelacionamento = new File(CAMINHO_CSV_RELACIONAMENTO);
            boolean escreverCabecalhoRelacionamento = !arquivoRelacionamento.exists() || arquivoRelacionamento.length() == 0;
            
            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivoRelacionamento, true))) {
                if (escreverCabecalhoRelacionamento) {
                    String[] cabecalho = {"id", "PedidoId", "ProdutoId"};
                    writer.writeNext(cabecalho);
                }
                
                for(Produto produto : pedido.getProdutos()) {
                    String[] dados = {
                        Integer.toString(obterProximoId(arquivoRelacionamento)),
                        Integer.toString(pedidoId),
                        Integer.toString(produto.getId())
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

                    List<Produto> produtos = buscarProdutosDoPedido(id, produtoRepository);
                    return Optional.of(new Pedido(id, nome, formaPagamento, produtos, taxas, descontos, modalidadeEntrega));
                }
            }

        } catch (IOException | CsvException e) {
            System.err.println("Erro ao buscar pedido por ID: " + e.getMessage());
        }

        return Optional.empty();
    }
    
    private List<Produto> buscarProdutosDoPedido(int pedidoId, ProdutoRepository produtoRepository) throws IOException, CsvException {
        List<Produto> produtos = new ArrayList<>();
        File arquivoRelacionamento = new File(CAMINHO_CSV_RELACIONAMENTO);
        if (!arquivoRelacionamento.exists()) return produtos;

        try (CSVReader reader = new CSVReader(new FileReader(arquivoRelacionamento))) {
            List<String[]> linhas = reader.readAll();
            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (Integer.parseInt(linha[1]) == pedidoId) {
                    int produtoId = Integer.parseInt(linha[2]);
                    
                    Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
                    if(produtoOpt.isPresent()) {
                        produtos.add(produtoOpt.get());
                    }
                }
            }
        }
        
        return produtos;
    }
    
    public List<Pedido> findAll() {
        ProdutoRepository produtoRepository = new ProdutoRepository();
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

                    List<Produto> produtos = buscarProdutosDoPedido(id, produtoRepository);
                    pedidos.add(new Pedido(id, nome, formaPagamento, produtos, taxas, descontos, modalidadeEntrega));
                }
            }

        } catch (IOException | CsvException e) {
            System.err.println("Erro ao buscar todos os pedidos: " + e.getMessage());
        }

        return pedidos;
    }
    
    private int obterProximoId(File arquivo) {
        int ultimoId = 0;
        if (!arquivo.exists()) {
            return 1;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();
            for (int i = 1; i < linhas.size(); i++) { // pula o cabeçalho
                String[] linha = linhas.get(i);
                if (linha.length > 0) {
                    try {
                        int idAtual = Integer.parseInt(linha[0]);
                        if (idAtual > ultimoId) {
                            ultimoId = idAtual;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler o CSV para obter o próximo ID: " + e.getMessage());
        }

        return ultimoId + 1;
    }
}
