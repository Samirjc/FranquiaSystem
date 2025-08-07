package ufjf_dcc025.franquiasystem.repositories;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public abstract class ArquivoRepository {
    protected int obterProximoId(File arquivo) {
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
