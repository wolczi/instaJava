package com.przemekwolczacki.instabot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginFrame {
    public static JFrame loginFrame;

    public JPasswordField passwordField;
    public JTextField loginField;
    public JPanel loginPanel;
    public JButton loginButton;

    private JLabel passwordLabel;
    private JLabel loginLabel;

    public LoginFrame() {
        InitializeFrame();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = GetLoginField();
                String password = GetPasswordField();

                try {
                    Chrome.LoginToInstagram(username, password);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void InitializeFrame(){
        loginFrame = new JFrame("Login Panel");
        loginFrame.setContentPane(loginPanel);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(new Dimension(400,300));
        loginFrame.setResizable(false);
        loginFrame.setVisible(true);
    }

    public String GetLoginField(){
        return loginField.getText();
    }

    public String GetPasswordField(){
        return new String(passwordField.getPassword());
    }

    public static void Close(){
        loginFrame.setVisible(false);
        loginFrame.dispose();
    }
}
