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
import ufjf_dcc025.franquiasystem.models.Dono;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Gerente;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.models.Vendedor;

public class UsuarioRepository {

    private static final String DIRETORIO = "data";
    private static final String CAMINHO_CSV = DIRETORIO + File.separator + "usuarios.csv";
    private static final String CAMINHO_CSV_FRANQUIA = DIRETORIO + File.separator + "franquias.csv";

    public Usuario create(Usuario usuario) {
        
        String franquiaId = "";
        if(usuario instanceof Vendedor) {
            franquiaId = Integer.toString(((Vendedor) usuario).getFranquia().getId());
        }
        
        try {
            File pasta = new File(DIRETORIO);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(CAMINHO_CSV);
            boolean escreverCabecalho = !arquivo.exists() || arquivo.length() == 0;
            usuario.setId(obterProximoId(arquivo));

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, true))) {
                if (escreverCabecalho) {
                    String[] cabecalho = {"id", "Nome", "senha", "tipo", "cpf", "email", "franquiaId"};
                    writer.writeNext(cabecalho);
                }

                String[] dados = {
                    Integer.toString(usuario.getId()),
                    usuario.getNome(),
                    usuario.getSenha(),
                    usuario.getTipo(),
                    usuario.getCpf(),
                    usuario.getEmail(),
                    franquiaId
                };

                writer.writeNext(dados);
            }

        } catch (IOException e) {
            System.err.println("Erro ao salvar franquia no CSV: " + e.getMessage());
        }
        
        return usuario;
    }

    public Optional<Usuario> findById(int id) {
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return Optional.empty();
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);
                if (linha.length >= 6) {

                    int idAtual = Integer.parseInt(linha[0]);
                    if (idAtual == id) {
                        String nome = linha[1];
                        String senha = linha[2];
                        String tipo = linha[3];
                        String cpf = linha[4];
                        String email = linha[5];
                        String franquiaId = linha[6];
                        
                        Franquia franquia = null;
                        if(!"".equals(franquiaId)) {
                            Optional<Franquia> franquiaOpt = findFranquiaById(Integer.parseInt(franquiaId));

                            if(franquiaOpt.isPresent()) {
                                franquia = franquiaOpt.get();
                            }
                        }
                        

                        switch (tipo) {
                            case "dono":
                                return Optional.of(new Dono(idAtual, nome, senha, cpf, email));
                            case "gerente":
                                return Optional.of(new Gerente(idAtual, nome, senha, cpf, email));
                            case "vendedor":
                                return Optional.of(new Vendedor(idAtual, nome, senha, cpf, email, franquia));
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

                if (linha.length >= 6) {
                    int idAtual = Integer.parseInt(linha[0]);
                    String nome = linha[1];
                    String senha = linha[2];
                    String tipo = linha[3];
                    String cpf = linha[4];
                    String email = linha[5];
                    String franquiaId = linha[6];

                    Franquia franquia = null;
                    if(!"".equals(franquiaId)) {
                        Optional<Franquia> franquiaOpt = findFranquiaById(Integer.parseInt(franquiaId));

                        if(franquiaOpt.isPresent()) {
                            franquia = franquiaOpt.get();
                        }
                    }

                    switch (tipo) {
                        case "dono":
                            usuarios.add(new Dono(idAtual, nome, senha, cpf, email));
                            break;
                        case "gerente":
                            usuarios.add(new Gerente(idAtual, nome, senha, cpf, email));
                            break;
                        case "vendedor":
                            usuarios.add(new Vendedor(idAtual, nome, senha, cpf, email, franquia));
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

    public boolean update(Usuario usuarioAtualizado) {
        String franquiaId = "";
        if(usuarioAtualizado instanceof Vendedor) {
            franquiaId = Integer.toString(((Vendedor) usuarioAtualizado).getFranquia().getId());
        }
        
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();
            boolean atualizado = false;

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);

                if (linha.length >= 6) {
                    int id = Integer.parseInt(linha[0]);

                    if (id == usuarioAtualizado.getId()) {
                        linhas.set(i, new String[]{
                            Integer.toString(usuarioAtualizado.getId()),
                            usuarioAtualizado.getNome(),
                            usuarioAtualizado.getSenha(),
                            usuarioAtualizado.getTipo(),
                            usuarioAtualizado.getCpf(),
                            usuarioAtualizado.getEmail(),
                            franquiaId
                        });
                        atualizado = true;
                        break;
                    }
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
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            return false;
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
    
    public Optional<Franquia> findFranquiaById(int id) {
        File arquivo = new File(CAMINHO_CSV_FRANQUIA);

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
                        String endereco = linha[2];
                        
                        return Optional.of(new Franquia(idAtual, nome, endereco, null));
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
}
