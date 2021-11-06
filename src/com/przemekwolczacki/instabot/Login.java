package com.przemekwolczacki.instabot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Login{
    public JPasswordField passwordField;
    public JTextField loginField;
    public JPanel loginPanel;
    public JButton loginButton;

    private JLabel passwordLabel;
    private JLabel loginLabel;

    public Login() {
        initiationFrame();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = getLoginField();
                String password = getPasswordField();

                Chrome.loginToInstagram(username, password);
            }
        });
    }

    public void  initiationFrame(){
        JFrame loginFrame = new JFrame("Login Panel");
        loginFrame.setContentPane(loginPanel);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(new Dimension(400,300));
        loginFrame.setResizable(false);
        loginFrame.setVisible(true);
    }

    public String getLoginField(){
        return loginField.getText();
    }

    public String getPasswordField(){
        return new String(passwordField.getPassword());
    }
}
