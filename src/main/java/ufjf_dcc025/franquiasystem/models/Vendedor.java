package ufjf_dcc025.franquiasystem.models;

public class Vendedor extends Usuario {

    public Vendedor(int id, String nome, String senha, String cpf, String email) {
        super(id, nome, senha, cpf, email);
    }
    public Vendedor(String nome, String senha, String cpf, String email) {
        super(nome, senha, cpf, email);
    }

    @Override
    public String getTipo() {
        return "vendedor";
    }
}
