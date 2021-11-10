package com.przemekwolczacki.instabot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;


public class LoginPanelFrame {
    public static JFrame loginPanelFrame;

    public JPasswordField passwordField;
    public JTextField loginField;
    public JPanel loginPanel;
    public JButton loginButton;

    private JLabel passwordLabel;
    private JLabel loginLabel;

    public LoginPanelFrame() {
        InitializeFrame();

        loginButton.addActionListener(e -> {
            String username = GetLoginField();
            String password = GetPasswordField();

            try {
                Chrome.LoginToInstagram(username, password);
            } catch (InterruptedException | SQLException ex) {
                ex.printStackTrace();
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        Chrome.LoginToInstagram(GetLoginField(), GetPasswordField());
                    } catch (InterruptedException | SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void InitializeFrame(){
        loginPanelFrame = new JFrame("instaJava / Logowanie");
        loginPanelFrame.setContentPane(loginPanel);
        loginPanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginPanelFrame.setSize(new Dimension(400,300));
        loginPanelFrame.setResizable(false);
        loginPanelFrame.setVisible(true);
    }

    private String GetLoginField(){
        return loginField.getText();
    }

    private String GetPasswordField(){
        return new String(passwordField.getPassword());
    }

    public static void Close(){
        loginPanelFrame.setVisible(false);
        loginPanelFrame.dispose();
    }
}
