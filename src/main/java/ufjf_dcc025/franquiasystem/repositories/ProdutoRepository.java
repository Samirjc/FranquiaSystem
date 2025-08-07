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
import ufjf_dcc025.franquiasystem.models.Produto;

public class ProdutoRepository extends ArquivoRepository{
    private static final String DIRETORIO = "data";
    private static final String CAMINHO_CSV = DIRETORIO + File.separator + "produtos.csv";

    public void create(Produto produto) {
        try {
            File pasta = new File(DIRETORIO);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(CAMINHO_CSV);
            boolean escreverCabecalho = !arquivo.exists() || arquivo.length() == 0;

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, true))) {
                if (escreverCabecalho) {
                    String[] cabecalho = {"id", "Nome", "preco", "quantidade"};
                    writer.writeNext(cabecalho);
                }

                String[] dados = {
                        Integer.toString(obterProximoId(arquivo)),
                        produto.getNome(),
                        Double.toString(produto.getPreco()),
                        Integer.toString(produto.getQuantidade())
                };

                writer.writeNext(dados);
            }

        } catch (IOException e) {
            System.err.println("Erro ao salvar produto no CSV: " + e.getMessage());
        }
    }
    
    public Optional<Produto> findById(int id) {
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return Optional.empty();
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (linha.length >= 4) {
                    int idAtual = Integer.parseInt(linha[0]);
                    if (idAtual == id) {
                        String nome = linha[1];
                        double preco = Double.parseDouble(linha[2]);
                        int quantidade = Integer.parseInt(linha[3]);
                        return Optional.of(new Produto(idAtual, nome, preco, quantidade));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o CSV para buscar produto por ID: " + e.getMessage());
        } catch (CsvException ex) {
            System.err.println("Erro ao ler o CSV para buscar produto por ID: " + ex.getMessage());
        }

        return Optional.empty();
    }
    
    public List<Produto> findAll() {
        List<Produto> produtos = new ArrayList<>();
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return produtos;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);

                if (linha.length >= 4) {
                    int idAtual = Integer.parseInt(linha[0]);
                    String nome = linha[1];
                    double preco = Double.parseDouble(linha[2]);
                    int quantidade = Integer.parseInt(linha[3]);


                    produtos.add(new Produto(idAtual, nome, preco, quantidade));
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Erro ao ler o CSV para buscar todos os usuários: " + e.getMessage());
        }

        return produtos;
    }
    
    public boolean update(Produto produtoAtualizado) {
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();
            boolean atualizado = false;

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);

                if (linha.length >= 4 && Integer.parseInt(linha[0]) == produtoAtualizado.getId()) { // Verifique por 4 colunas
                    linhas.set(i, new String[]{
                            Integer.toString(produtoAtualizado.getId()),
                            produtoAtualizado.getNome(),
                            Double.toString(produtoAtualizado.getPreco()),
                            Integer.toString(produtoAtualizado.getQuantidade()) // Adicione a quantidade
                    });
                    atualizado = true;
                    break;
                }
            }

            if (!atualizado) {
                return false;
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, false))) {
                for (String[] linha : linhas) {
                    writer.writeNext(linha);
                }
            }

            return true;
        } catch (IOException | CsvException e) {
            return false;
        }
    }
    
    public boolean delete(int id) {
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();
            boolean removido = false;
            
            List<String[]> novasLinhas = new ArrayList<>();
            novasLinhas.add(linhas.get(0));

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (linha.length >= 1 && Integer.parseInt(linha[0]) != id) {
                    novasLinhas.add(linha);
                } else if (linha.length >= 1 && Integer.parseInt(linha[0]) == id) {
                    removido = true;
                }
            }

            if (!removido) {
                return false;
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, false))) {
                for (String[] linha : novasLinhas) {
                    writer.writeNext(linha);
                }
            }

            return true;

        } catch (IOException | CsvException e) {
            System.err.println("Erro ao deletar produto: " + e.getMessage());
            return false;
        }
    }
}
