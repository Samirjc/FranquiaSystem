package ufjf_dcc025.franquiasystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.exceptions.UsuarioNaoEncontradoException;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Gerente;
import ufjf_dcc025.franquiasystem.models.Usuario;
import ufjf_dcc025.franquiasystem.repositories.FranquiaRepository;
import ufjf_dcc025.franquiasystem.repositories.UsuarioRepository;

public class FranquiaSystem {

    public static void main(String[] args) {
        UsuarioController controller = new UsuarioController();
        
        try {
            Usuario usuario = controller.findById(2);
            System.err.println(usuario.getTipo());
            
        } catch (UsuarioNaoEncontradoException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
