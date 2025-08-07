package ufjf_dcc025.franquiasystem;
import java.util.List;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.dto.VendedorRankingDTO;
import ufjf_dcc025.franquiasystem.models.Vendedor;
import ufjf_dcc025.franquiasystem.views.LoginView;

public class FranquiaSystem {
    
    public static void main(String[] args) {
        UsuarioController controller = new UsuarioController();
        List<VendedorRankingDTO> vendedores = controller.findAllVendedoresByFranquiaId(3, "quantidade");
        
        for(VendedorRankingDTO vendedor : vendedores) {
            System.out.println("--------------------------------");
            System.out.println(vendedor.getId());
            System.out.println(vendedor.getNome());
            System.out.println(vendedor.getEmail());
            System.out.println(vendedor.getQuantidadeVendas());
            System.out.println(vendedor.getValorTotalVendas());
            System.out.println("--------------------------------");
        }
        
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}
