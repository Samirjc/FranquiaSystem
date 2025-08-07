package ufjf_dcc025.franquiasystem.controllers;

import java.util.List;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.exceptions.ErroCriacaoFranquiaException;
import ufjf_dcc025.franquiasystem.repositories.FranquiaRepository;
import ufjf_dcc025.franquiasystem.models.Franquia;
import java.util.stream.Collectors;


public class FranquiaController {
    private final FranquiaRepository franquiaRepository;
    
    public FranquiaController() {
        franquiaRepository = new FranquiaRepository();
    }
    
    public Franquia create(Franquia franquia) throws ErroCriacaoFranquiaException {
        Optional<Franquia> franquiaCriada = franquiaRepository.create(franquia);
        
        if(franquiaCriada.isPresent()) {
            return franquiaCriada.get();
        }else{
            throw new ErroCriacaoFranquiaException();
        }
    }
    
    public List<Franquia> findAll() {
        return franquiaRepository.findAll();
    }
    
    public List<Franquia> findAllSemGerente() {
        return franquiaRepository.findAll()
                .stream()
                .filter(franquia -> franquia.getGerente() == null)
                .collect(Collectors.toList());
    }
    
    public void update(Franquia franquiaAtualizada) {
        franquiaRepository.update(franquiaAtualizada);
    }
    
    public void delete(int id) {
        franquiaRepository.delete(id);
    }

    public Optional<Franquia> findFranquiaByGerenteId(int gerenteId) {
        return franquiaRepository.findAll().stream()
                .filter(f -> f.getGerente() != null && f.getGerente().getId() == gerenteId)
                .findFirst();
    }
}
