package ufjf_dcc025.franquiasystem.models;

public class Dono extends Usuario {

    public Dono(int id, String nome, String senha, String cpf, String email) {
        super(id, nome, senha, cpf, email);
    }
    
    public Dono(String nome, String senha, String cpf, String email) {
        super(nome, senha, cpf, email);
    }

    @Override
    public String getTipo() {
        return "dono";
    }
}
