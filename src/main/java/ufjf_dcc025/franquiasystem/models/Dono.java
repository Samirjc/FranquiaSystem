package ufjf_dcc025.franquiasystem.models;

import java.util.List;

public class Dono extends Usuario {
    List<Franquia> franquias;
    
    public void addFranquia(Franquia franquia) {
        franquias.add(franquia);
    }

    @Override
    public String getTipo() {
        return "dono";
    }
}
