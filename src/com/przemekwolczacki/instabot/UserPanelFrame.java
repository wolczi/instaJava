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

    public String url;

    int[] statCounters;

    public UserPanelFrame() throws SQLException {
        InitializeFrame();

        Database db = new Database();

        SetInfoPoolLabel();
        SetInfoRemoveLabels();
        SetInfoFollowsLikesLabels();

        Database.RemoveOtherDaysStats();

        saveToDatabaseButton.addActionListener(e -> {
            try {
                Database.AddingToDbMessage(eventLogArea);

                AddToPool();
                SetInfoPoolLabel();

                Database.EndOfAddingToDbMessage(eventLogArea);
            } catch (InterruptedException | SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void InitializeFrame(){
        userPanelFrame = new JFrame("instaJava / Panel Użytkownika");
        userPanelFrame.setContentPane(userPanel);
        userPanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userPanelFrame.setSize(new Dimension(800,700));
        userPanelFrame.setResizable(false);
        userPanelFrame.setVisible(true);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        eventLogArea.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 0,0,0)));
    }

    public void SetInfoFollowsLikesLabels() throws SQLException {
        statCounters = Database.CheckTodayStats();
        infoFollowsLabel.setText("Zaobserwowano: " + statCounters[0]);
        infoLikesLabel.setText("Polubiono: " + statCounters[1]);
    }

    public void SetInfoRemoveLabels() throws SQLException {
        statCounters = Database.CountObservationsToRemove();
        infoRemoveLabel2.setText("ogólnie: " + statCounters[0]);
        infoRemoveLabel3.setText("dłużej niż trzy dni: " + statCounters[1]);
    }

    public void SetInfoPoolLabel() throws SQLException {
        infoPoolLabel.setText("Liczba kont w bazie (nieodwiedzonych): " + Database.CountProfilesInPool());
    }

    public void AddToPool() throws InterruptedException, SQLException {
        url = linkToProfileField.getText();

        Chrome.GoToURL(url);
        Chrome.DownloadSomeonesFollowers(eventLogArea);
    }

}
