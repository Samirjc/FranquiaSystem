package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.models.Usuario;
import javax.swing.*;

public class GerenteView extends JFrame{
    public GerenteView(Usuario dono) {
        setTitle("Painel do Gerente");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel label = new JLabel("Bem-vindo, Gerente " + dono.getNome(), SwingConstants.CENTER);
        add(label);
    }
}
