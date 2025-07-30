package ufjf_dcc025.franquiasystem;
import ufjf_dcc025.franquiasystem.models.Franquia;
import ufjf_dcc025.franquiasystem.models.Gerente;
import ufjf_dcc025.franquiasystem.repositories.FranquiaRepository;
import ufjf_dcc025.franquiasystem.repositories.UsuarioRepository;

public class FranquiaSystem {

    public static void main(String[] args) {
        FranquiaRepository repository = new FranquiaRepository();;;
        
        Gerente gerente = new Gerente();
        gerente.setId(1);
        gerente.setNome("pedro");
        gerente.setSenha("senha123");
        Franquia franquia = new Franquia("juiz de, fora", "endereco", gerente);
        
        repository.create(franquia);
        
        /*UsuarioRepository userRepository = new UsuarioRepository();
        Gerente gerente = new Gerente();
        gerente.setNome("pedro");
        gerente.setSenha("senha123");
        userRepository.create(gerente);*/
    }
}
