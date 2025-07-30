package ufjf_dcc025.franquiasystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.exceptions.TipoDeUsuarioInvalido;

public class FranquiaSystem {

    public static void main(String[] args) {
        UsuarioController controller = new UsuarioController();
        
        try {
            controller.create("Samir", "password321", "dono");
        } catch (TipoDeUsuarioInvalido ex) {
            Logger.getLogger(FranquiaSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
