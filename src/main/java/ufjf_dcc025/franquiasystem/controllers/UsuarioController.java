package ufjf_dcc025.franquiasystem.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.exceptions.TipoDeUsuarioInvalido;
import ufjf_dcc025.franquiasystem.exceptions.UsuarioNaoEncontradoException;
import ufjf_dcc025.franquiasystem.models.Dono;
import ufjf_dcc025.franquiasystem.models.Gerente;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.models.Vendedor;
import ufjf_dcc025.franquiasystem.repositories.UsuarioRepository;

public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioController() {
        this.usuarioRepository = new UsuarioRepository();
    }
    
    public Optional<Usuario> autenticar(String nome, String senha) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        for(Usuario usuario : usuarios) {
            if(usuario.getNome().equals(nome) && usuario.getSenha().equals(senha)) {
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
    
    public void create(String nome, String senha, String tipo) throws TipoDeUsuarioInvalido {
        switch (tipo) {
            case "dono":
                usuarioRepository.create(new Dono(nome, senha));
                break;
            case "gerente":
                usuarioRepository.create(new Gerente(nome, senha));
                break;
            case "vendedor":
                usuarioRepository.create(new Vendedor(nome, senha));
                break;
            default:
                throw new TipoDeUsuarioInvalido();
        }
    }
    
    public void update(int id, String nome, String senha, String tipo) throws TipoDeUsuarioInvalido {
        switch (tipo) {
            case "dono":
                usuarioRepository.update(new Dono(id, nome, senha));
                break;
            case "gerente":
                usuarioRepository.update(new Gerente(id, nome, senha));
                break;
            case "vendedor":
                usuarioRepository.update(new Vendedor(id, nome, senha));
                break;
            default:
                throw new TipoDeUsuarioInvalido();
        }
    }
    
    public void delete(int id) {
        usuarioRepository.delete(id);
    }
}
