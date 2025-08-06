package ufjf_dcc025.franquiasystem.models;

public class Vendedor extends Usuario {
    private Franquia franquia;

    public Vendedor(int id, String nome, String senha, String cpf, String email, Franquia franquia) {
        super(id, nome, senha, cpf, email);
        this.franquia = franquia;
    }
    public Vendedor(String nome, String senha, String cpf, String email, Franquia franquia) {
        super(nome, senha, cpf, email);
        this.franquia = franquia;
    }
    
    public Franquia getFranquia() {
        return franquia;
    }
    public void setFranquia(Franquia franquia) {
        this.franquia = franquia;
    }

    @Override
    public String getTipo() {
        return "vendedor";
    }
}
