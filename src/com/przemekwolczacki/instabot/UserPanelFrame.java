package com.przemekwolczacki.instabot;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;

public class UserPanelFrame {
    public static JFrame userPanelFrame;
    public JPanel userPanel;

    private JTextArea eventLogArea;
    private JTextField linkToProfileField;
    private JTextField hashtagField;
    private JButton saveToDatabaseButton;
    private JButton stopButton;
    private JButton removeFollowsButton;
    private JButton workWithHashtagButton;
    private JButton workWithAccountsPoolButton;
    private JLabel statsLabel;
    private JLabel infoLikesLabel;
    private JLabel infoFollowsLabel;
    private JLabel infoRemoveLabel1;
    private JLabel infoRemoveLabel2;
    private JLabel infoRemoveLabel3;
    private JLabel infoPoolLabel;

    public UserPanelFrame() throws SQLException {
        userPanelFrame = new JFrame("instaJava / Panel Użytkownika");
        userPanelFrame.setContentPane(userPanel);
        userPanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userPanelFrame.setSize(new Dimension(800,700));
        userPanelFrame.setResizable(false);
        userPanelFrame.setVisible(true);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        eventLogArea.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 0,0,0)));

        infoPoolLabel.setText("Liczba kont w bazie (nieodwiedzonych): " + Database.checkPoolSize());
    }
}
