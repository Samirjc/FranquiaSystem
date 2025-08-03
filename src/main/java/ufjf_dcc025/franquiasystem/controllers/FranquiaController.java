package ufjf_dcc025.franquiasystem.controllers;

import java.util.List;
import ufjf_dcc025.franquiasystem.repositories.FranquiaRepository;
import ufjf_dcc025.franquiasystem.models.Franquia;

public class FranquiaController {
    private final FranquiaRepository franquiaRepository;
    
    public FranquiaController() {
        franquiaRepository = new FranquiaRepository();
    }
    
    public void create(Franquia franquia) {
        franquiaRepository.create(franquia);
    }
    
    public List<Franquia> findAll() {
        return franquiaRepository.findAll();
    }
    
    public void update(Franquia franquiaAtualizada) {
        franquiaRepository.update(franquiaAtualizada);
    }
    
    public void delete(int id) {
        franquiaRepository.delete(id);
    }
}
