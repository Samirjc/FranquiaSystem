package ufjf_dcc025.franquiasystem.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.exceptions.UsuarioNaoEncontradoException;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.repositories.UsuarioRepository;

public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioController() {
        this.usuarioRepository = new UsuarioRepository();
    }
    
    public Optional<Usuario> autenticar(String email, String senha) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        for(Usuario usuario : usuarios) {
            if(usuario.getEmail().equals(email) && usuario.getSenha().equals(senha)) {
                return Optional.of(usuario);
            }
        }
        
        return Optional.empty();
    }
    
    public Usuario findById(int id) throws UsuarioNaoEncontradoException {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        
        if(usuario.isPresent()) {
            return usuario.get();
        }else {
            throw new UsuarioNaoEncontradoException();
        }
    }
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public List<Usuario> findAllGerentes() {
        return usuarioRepository.findAll().stream().filter(u -> "gerente".equals(u.getTipo())).collect(Collectors.toList());
    }
    
    public void delete(int id) {
        usuarioRepository.delete(id);
    }
}
