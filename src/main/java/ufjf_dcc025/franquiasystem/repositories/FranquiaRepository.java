package ufjf_dcc025.franquiasystem.repositories;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import ufjf_dcc025.franquiasystem.models.Franquia;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FranquiaRepository {
    private static final String DIRETORIO = "data";
    private static final String CAMINHO_CSV = DIRETORIO + File.separator + "franquias.csv";

    public void create(Franquia franquia) {
        try {
            File pasta = new File(DIRETORIO);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(CAMINHO_CSV);
            boolean escreverCabecalho = !arquivo.exists() || arquivo.length() == 0;

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, true))) {
                if (escreverCabecalho) {
                    String[] cabecalho = { "id", "Nome", "Endereco", "gerenteId" };
                    writer.writeNext(cabecalho);
                }

                String[] dados = {
                    Integer.toString(obterProximoId(arquivo)),
                    franquia.getNome(),
                    franquia.getEndereco(),
                    Integer.toString(franquia.getGerente().getId())
                };

                writer.writeNext(dados);
            }

        } catch (IOException e) {
            System.err.println("Erro ao salvar franquia no CSV: " + e.getMessage());
        }
    }
    
    private int obterProximoId(File arquivo) {
        int ultimoId = 0;
        if (!arquivo.exists()) return 1;

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
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler o CSV para obter o próximo ID: " + e.getMessage());
        }

        return ultimoId + 1;
    }
}
