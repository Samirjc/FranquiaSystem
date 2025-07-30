package ufjf_dcc025.franquiasystem.models;

public class Vendedor extends Usuario {
    private Franquia franquia;

    public Vendedor(int id, String nome, String senha) {
        super(id, nome, senha);
    }

    @Override
    public String getTipo() {
        return "vendedor";
    }
}
