package ufjf_dcc025.franquiasystem.controllers;

import java.util.Optional;
import ufjf_dcc025.franquiasystem.exceptions.UsuarioNaoEncontradoException;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.repositories.UsuarioRepository;

public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioController() {
        this.usuarioRepository = new UsuarioRepository();
    }
    
    public Usuario findById(int id) throws UsuarioNaoEncontradoException {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        
        if(usuario.isPresent()) {
            return usuario.get();
        }else {
            throw new UsuarioNaoEncontradoException();
        }
    }
}
