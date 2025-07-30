package ufjf_dcc025.franquiasystem.models;

public class Vendedor extends Usuario {
    private Franquia franquia;

    @Override
    public String getTipo() {
        return "vendedor";
    }
}
