package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
    }

    public void InitializeFrame(){
        loginPanelFrame = new JFrame("instaJava / Logowanie");
        loginPanelFrame.setContentPane(loginPanel);
        loginPanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginPanelFrame.setSize(new Dimension(400,300));
        loginPanelFrame.setResizable(false);
        loginPanelFrame.setVisible(true);

        loginButton.addActionListener(e -> {
            String username = GetLoginField();
            String password = GetPasswordField();

            try {
                LoginToInstagram(username, password);
            } catch (InterruptedException | SQLException ex) {
                ex.printStackTrace();
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        LoginToInstagram(GetLoginField(), GetPasswordField());
                    } catch (InterruptedException | SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
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

    public void LoginToInstagram(String username, String password) throws InterruptedException, SQLException {
        WebElement loginBox = Chrome.driver.findElement(By.xpath("//input[@name='username']"));
        loginBox.click();
        loginBox.sendKeys(username); Bot.ActionPause(2);

        WebElement passwordBox = Chrome.driver.findElement(By.xpath("//input[@name='password']"));
        passwordBox.click();
        passwordBox.sendKeys(password); Bot.ActionPause(2);

        Chrome.driver.findElement(By.xpath("//button[contains(@type,'submit')]")).click();

        LoginVerification(loginBox, passwordBox);
    }

    private void LoginVerification(WebElement loginBox, WebElement passwordBox) throws SQLException {
        Bot.ActionPause(5);

        try{
            Chrome.driver.findElement(By.xpath("//input[@name='username']"));

            JOptionPane.showMessageDialog(null, "Złe dane logowania!",
                    "Błąd logowania", JOptionPane.INFORMATION_MESSAGE);

            Chrome.ClearLoginFieldsOnPage(loginBox, passwordBox);
        }
        catch(Exception e){
            LoginPanelFrame.Close();

            try {
                Chrome.driver.findElement(By.xpath("//button[normalize-space()='Nie teraz']")).click();
                Bot.ActionPause(3);
                Chrome.driver.findElement(By.xpath("//*[text()='Nie teraz']")).click();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }

            Bot.DisplayUserPanel();
        }

    }
}
