package ufjf_dcc025.franquiasystem.models;

public class Gerente extends Usuario {

    public Gerente(int id, String nome, String senha) {
        super(id, nome, senha);
    }
    
    @Override
    public String getTipo() {
        return "gerente";
    }
}
