package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.models.Usuario;
import javax.swing.*;

public class DonoView extends JFrame{
    public DonoView(Usuario dono) {
        setTitle("Painel do Dono");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel label = new JLabel("Bem-vindo, Administrador " + dono.getNome(), SwingConstants.CENTER);
        add(label);
    }
}
