package ufjf_dcc025.franquiasystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.exceptions.TipoDeUsuarioInvalido;

public class FranquiaSystem {

    public static void main(String[] args) {
        UsuarioController controller = new UsuarioController();
        
        controller.delete(1);
    }
}
