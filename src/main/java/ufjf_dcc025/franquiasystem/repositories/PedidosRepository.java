package ufjf_dcc025.franquiasystem.repositories;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
