package ufjf_dcc025.franquiasystem.models;

public class Gerente extends Usuario {
    
    @Override
    public String getTipo() {
        return "gerente";
    }
}
