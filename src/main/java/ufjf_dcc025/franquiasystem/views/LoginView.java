package ufjf_dcc025.franquiasystem.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import ufjf_dcc025.franquiasystem.controllers.UsuarioController;
import ufjf_dcc025.franquiasystem.models.Usuario;

public class LoginView extends JFrame {
    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private UsuarioController usuarioController;

    public LoginView() {
        usuarioController = new UsuarioController();

        setTitle("Login");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel principal com GridBagLayout
        JPanel painelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoLogin = new JTextField(30);
        campoSenha = new JPasswordField(30);
        JButton botaoLogin = new JButton("Entrar");

        JLabel labelLogin = new JLabel("Email:");
        JLabel labelSenha = new JLabel("Senha:");

        JLabel labelTitulo = new JLabel("Franquia System", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));

        labelLogin.setFont(new Font("SansSerif", Font.PLAIN, 18));
        labelSenha.setFont(new Font("SansSerif", Font.PLAIN, 18));
        campoLogin.setFont(new Font("SansSerif", Font.PLAIN, 16));
        campoSenha.setFont(new Font("SansSerif", Font.PLAIN, 16));
        botaoLogin.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Linha 0 - Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa as duas colunas
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza
        painelForm.add(labelTitulo, gbc);

        // Reseta o gridwidth para os próximos componentes
        gbc.gridwidth = 1;

        // Linha 1 - Email (agora na linha 1)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Alinha o texto à esquerda
        painelForm.add(labelLogin, gbc);

        gbc.gridx = 1;
        painelForm.add(campoLogin, gbc);

        // Linha 2 - Senha (agora na linha 2)
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelForm.add(labelSenha, gbc);

        gbc.gridx = 1;
        painelForm.add(campoSenha, gbc);

        // Linha 3 - Botão (agora na linha 3)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelForm.add(botaoLogin, gbc);

        // Painel para centralizar o formulário dentro da janela
        JPanel container = new JPanel(new GridBagLayout());
        container.add(painelForm);

        add(container);

        botaoLogin.addActionListener(this::autenticar);
    }

    private void autenticar(ActionEvent e) {
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());

        Optional<Usuario> usuarioOpt = usuarioController.autenticar(login, senha);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getNome());
            abrirTelaPorPerfil(usuario);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaPorPerfil(Usuario usuario) {
        switch (usuario.getTipo()) {
            case "dono":
                new DonoView(usuario).setVisible(true);
                break;
            case "gerente":
                new GerenteView(usuario).setVisible(true);
                break;
            case "vendedor":
                new VendedorView(usuario).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Perfil desconhecido.");
        }
    }
}
