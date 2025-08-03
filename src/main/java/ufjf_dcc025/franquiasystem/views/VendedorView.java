package ufjf_dcc025.franquiasystem.views;

import ufjf_dcc025.franquiasystem.models.Usuario;
import javax.swing.*;

public class VendedorView extends JFrame{
    public VendedorView(Usuario dono) {
        setTitle("Painel do Vendedor");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel label = new JLabel("Bem-vindo, Vendedor " + dono.getNome(), SwingConstants.CENTER);
        add(label);
    }
}
