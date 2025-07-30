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
import java.util.logging.Level;
import java.util.logging.Logger;
import ufjf_dcc025.franquiasystem.models.Dono;
import ufjf_dcc025.franquiasystem.models.Gerente;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.models.Vendedor;

public class UsuarioRepository {

    private static final String DIRETORIO = "data";
    private static final String CAMINHO_CSV = DIRETORIO + File.separator + "usuarios.csv";

    public void create(Usuario usuario) {
        try {
            File pasta = new File(DIRETORIO);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(CAMINHO_CSV);
            boolean escreverCabecalho = !arquivo.exists() || arquivo.length() == 0;

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, true))) {
                if (escreverCabecalho) {
                    String[] cabecalho = {"id", "Nome", "senha", "tipo"};
                    writer.writeNext(cabecalho);
                }

                String[] dados = {
                    Integer.toString(obterProximoId(arquivo)),
                    usuario.getNome(),
                    usuario.getSenha(),
                    usuario.getTipo()
                };

                writer.writeNext(dados);
            }

        } catch (IOException e) {
            System.err.println("Erro ao salvar franquia no CSV: " + e.getMessage());
        }
    }

    public Optional<Usuario> findById(int id) {
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return null;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (linha.length >= 4) {

                    int idAtual = Integer.parseInt(linha[0]);
                    if (idAtual == id) {
                        String nome = linha[1];
                        String senha = linha[2];
                        String tipo = linha[3];

                        switch (tipo) {
                            case "dono":
                                return Optional.of(new Dono(idAtual, nome, senha));
                            case "gerente":
                                return Optional.of(new Gerente(idAtual, nome, senha));
                            case "vendedor":
                                return Optional.of(new Vendedor(idAtual, nome, senha));
                            default:
                                throw new AssertionError();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o CSV para buscar usuário por ID: " + e.getMessage());
        } catch (CsvException ex) {
            System.err.println("Erro ao ler o CSV para buscar usuário por ID: " + ex.getMessage());
        }

        return Optional.empty();
    }

    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return usuarios;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);

                if (linha.length >= 4) {
                    int idAtual = Integer.parseInt(linha[0]);
                    String nome = linha[1];
                    String senha = linha[2];
                    String tipo = linha[3];

                    switch (tipo) {
                        case "dono":
                            usuarios.add(new Dono(idAtual, nome, senha));
                            break;
                        case "gerente":
                            usuarios.add(new Gerente(idAtual, nome, senha));
                            break;
                        case "vendedor":
                            usuarios.add(new Vendedor(idAtual, nome, senha));
                            break;
                        default:
                            System.err.println("Tipo de usuário desconhecido: " + tipo);
                            break;
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Erro ao ler o CSV para buscar todos os usuários: " + e.getMessage());
        }

        return usuarios;
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
