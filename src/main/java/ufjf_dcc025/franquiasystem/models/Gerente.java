package ufjf_dcc025.franquiasystem.models;

public class Gerente extends Usuario {

    public Gerente(int id, String nome, String senha, String cpf, String email) {
        super(id, nome, senha, cpf, email);
    }
    
    public Gerente(String nome, String senha, String cpf, String email) {
        super(nome, senha, cpf, email);
    }
    
    @Override
    public String getTipo() {
        return "gerente";
    }
}
