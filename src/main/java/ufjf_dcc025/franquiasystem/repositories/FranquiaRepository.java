package ufjf_dcc025.franquiasystem.repositories;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Usuario;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FranquiaRepository extends ArquivoRepository{

    private static final String DIRETORIO = "data";
    private static final String CAMINHO_CSV = DIRETORIO + File.separator + "franquias.csv";

    public Optional<Franquia> create(Franquia franquia) {
        Usuario gerente = null;

        if (franquia.getGerente() != null) {
            UsuarioRepository usuarioRepository = new UsuarioRepository();
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(franquia.getGerente().getId());

            if (usuarioOpt.isPresent()) {
                gerente = usuarioOpt.get();
            } else {
                gerente = usuarioRepository.create(franquia.getGerente());
            }
        }

        try {
            File pasta = new File(DIRETORIO);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            File arquivo = new File(CAMINHO_CSV);
            boolean escreverCabecalho = !arquivo.exists() || arquivo.length() == 0;

            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivo, true))) {
                if (escreverCabecalho) {
                    String[] cabecalho = {"id", "Nome", "Endereco", "gerenteId"};
                    writer.writeNext(cabecalho);
                }

                int franquiaAtualizadaId = obterProximoId(arquivo);
                franquia.setId(franquiaAtualizadaId);
                String[] dados = {
                    Integer.toString(franquiaAtualizadaId),
                    franquia.getNome(),
                    franquia.getEndereco(),
                    gerente != null ? Integer.toString(gerente.getId()) : ""
                };

                writer.writeNext(dados);
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar franquia no CSV: " + e.getMessage());
            return Optional.empty();
        }
        
        return Optional.of(franquia);
    }

    public Optional<Franquia> findById(int id) {
        UsuarioRepository usuarioRepository = new UsuarioRepository();

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
                        String endereco = linha[2];
                        String gerenteId = linha[3];

                        if (!"".equals(gerenteId)) {
                            Optional<Usuario> gerenteOpt = usuarioRepository.findById(Integer.parseInt(gerenteId));

                            if (gerenteOpt.isPresent()) {
                                return Optional.of(new Franquia(idAtual, nome, endereco, gerenteOpt.get()));
                            }
                        } else {
                            return Optional.of(new Franquia(idAtual, nome, endereco, null));
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

    public List<Franquia> findAll() {
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        List<Franquia> franquias = new ArrayList<>();
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return franquias;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);

                if (linha.length >= 4) {
                    int idAtual = Integer.parseInt(linha[0]);
                    String nome = linha[1];
                    String endereco = linha[2];
                    String gerenteId = linha[3];

                    if (!"".equals(gerenteId)) {
                        Optional<Usuario> gerenteOpt = usuarioRepository.findById(Integer.parseInt(gerenteId));

                        if (gerenteOpt.isPresent()) {
                            franquias.add(new Franquia(idAtual, nome, endereco, gerenteOpt.get()));
                        }
                    } else {
                        franquias.add(new Franquia(idAtual, nome, endereco, null));
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Erro ao ler o CSV para buscar todos os usuários: " + e.getMessage());
        }

        return franquias;
    }
    
    public boolean update(Franquia franquiaAtualizada) {
        File arquivo = new File(CAMINHO_CSV);

        if (!arquivo.exists()) {
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();
            boolean atualizado = false;

            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);

                if (linha.length >= 4) {
                    int id = Integer.parseInt(linha[0]);

                    if (id == franquiaAtualizada.getId()) {
                        linhas.set(i, new String[]{
                            Integer.toString(franquiaAtualizada.getId()),
                            franquiaAtualizada.getNome(),
                            franquiaAtualizada.getEndereco(),
                            franquiaAtualizada.getGerente() != null ? Integer.toString(franquiaAtualizada.getGerente().getId()) : linha[3]
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
}
