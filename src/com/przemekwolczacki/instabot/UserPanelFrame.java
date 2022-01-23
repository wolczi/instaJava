package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserPanelFrame {
    public static JFrame userPanelFrame;
    public JPanel userPanel;

    private JTextArea eventLogArea;
    private JTextField linkToProfileField;
    private JButton saveToDatabaseButton;
    private JButton removeFollowsButton;
    private JButton workWithAccountsPoolButton;
    private JLabel statsLabel;
    public JLabel infoLikesLabel;
    public JLabel infoFollowsLabel;
    private JLabel infoRemoveLabel1;
    public JLabel infoRemoveLabel2;
    public JLabel infoRemoveLabel3;
    public JLabel infoPoolLabel;
    private JButton stopButton;

    public String url;

    public SwingWorker sw1 = null;
    public SwingWorker sw2 = null;
    public SwingWorker sw3 = null;

    public UserPanelFrame() throws SQLException {
        InitializeFrame();

        Database db = new Database();
        Database.RemoveOtherDaysStats();

        SetInfoPoolLabel();
        SetInfoRemoveLabels();
        SetInfoLikesLabel();
        SetInfoFollowsLabel();
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

        saveToDatabaseButton.addActionListener(e -> {
            sw1 = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    try {
                        if(linkToProfileField.getText().equals("https://www.instagram.com/link_do_profilu/")){
                            EventLogger.WrongLinkToProfile(eventLogArea);
                        }
                        else{
                            EventLogger.AddingToDbMessage(eventLogArea);

                            DownloadSomeonesFollowers();
                            SetInfoPoolLabel();

                            EventLogger.EndOfAddingToDbMessage(eventLogArea);
                        }
                    } catch (InterruptedException | SQLException ex) {
                        ex.printStackTrace();
                    }

                    return sw1= null;
                }
            };

            sw1.execute();
        });

        removeFollowsButton.addActionListener(e -> {
            sw2 = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    try {
                        if (Database.moreThan3Days > 0) {

                            EventLogger.StartDeleteObservationsMessage(eventLogArea);

                            ClearObservationList();
                            SetInfoRemoveLabels();

                            EventLogger.EndDeleteObservationsMessage(eventLogArea);
                        }
                        else {
                            EventLogger.NoOneToRemoveMessage(eventLogArea);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    return sw2 = null;
                }
            };

            sw2.execute();
        });
        workWithAccountsPoolButton.addActionListener(e -> {
            sw3 = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    try {
                        int todayFollowsCounter = Database.CheckTodayFollows();
                        int accountsInPool = Database.CountProfilesInPool();

                        if (accountsInPool > 0) {
                            if(todayFollowsCounter < 200){
                                EventLogger.StartFollowLikePoolMessage(eventLogArea);
                                WorkWithPool();
                                EventLogger.EndFollowLikePoolMessage(eventLogArea, 2);
                            }
                            else{
                                EventLogger.EndFollowLikePoolMessage(eventLogArea, 1);
                            }
                        }
                        else{
                            EventLogger.EndFollowLikePoolMessage(eventLogArea, 0);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    return sw3 = null;
                }
            };

            sw3.execute();
        });

        stopButton.addActionListener(e -> {
            if(sw1 != null){
                try {
                    sw1.cancel(true);
                    sw1 = null;
                    SetInfoPoolLabel();
                    EventLogger.CancelAddToPoolMessage(eventLogArea);
                } catch(Exception c){}
            }
            else if(sw2 != null) {
                try {
                    sw2.cancel(true);
                    sw2 = null;
                    SetInfoRemoveLabels();
                    EventLogger.CancelRemoveFollowsMessage(eventLogArea);
                } catch (Exception c) {}
            }
            else if(sw3 != null) {
                try {
                    sw3.cancel(true);
                    sw3 = null;
                    SetInfoFollowsLabel();
                    SetInfoLikesLabel();
                    EventLogger.CancelWorkWithPoolMessage(eventLogArea);
                } catch (Exception c) {}
            }
            else {
                EventLogger.NoActivitiesToStop(eventLogArea);
            }

        });
    }

    public void SetInfoFollowsLabel() throws SQLException {
        infoFollowsLabel.setText("Zaobserwowano: " + Database.CheckTodayFollows());

        infoFollowsLabel.paintImmediately(infoFollowsLabel.getVisibleRect());
    }

    public void SetInfoLikesLabel() throws SQLException {
        infoLikesLabel.setText("Polubiono: " + Database.CheckTodayLikes());

        infoLikesLabel.paintImmediately(infoLikesLabel.getVisibleRect());
    }

    public void SetInfoRemoveLabels() throws SQLException {
        int[] statCounters = Database.CountObservationsToRemove();
        infoRemoveLabel2.setText("ogólnie: " + statCounters[0]);
        infoRemoveLabel3.setText("dłużej niż trzy dni: " + statCounters[1]);

        infoRemoveLabel2.paintImmediately(infoRemoveLabel2.getVisibleRect());
        infoRemoveLabel3.paintImmediately(infoRemoveLabel3.getVisibleRect());
    }

    public void SetInfoPoolLabel() throws SQLException {
        infoPoolLabel.setText("Liczba kont w bazie (nieodwiedzonych): " + Database.CountProfilesInPool());

        infoPoolLabel.paintImmediately(infoPoolLabel.getVisibleRect());
    }

    public void DownloadSomeonesFollowers() throws InterruptedException, SQLException {
        url = linkToProfileField.getText();
        Chrome.GoToURL(url);

        double someonesFollowers = Chrome.GetFollowersNumberDouble();

        Chrome.FollowersListClick();
        WebElement fBody = Chrome.FocusFollowersList();
        Chrome.ScrollList(fBody, someonesFollowers);
        List<WebElement> buttons = Chrome.GetButtonsFromList();
        List<WebElement> users = Chrome.GetUsernamesFromList();

        int i = 0;
        for (WebElement user : users) {
            try {
                if (Database.CountProfilesInPool() < 1000) {
                    if (buttons.get(i).getText().equals("Obserwuj")) {
                        Database.AddProfileToPool(user.getText(), user.getAttribute("href"));
                    }
                } else {
                    EventLogger.ReachedUserLimitInDbMessage(eventLogArea);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            i = i + 1;
        }
    }

    public void ClearObservationList() throws SQLException {
        url = "https://www.instagram.com/przemek_wolczi/";
        Chrome.GoToURL(url);

        double iamFollowing = Chrome.GetFollowingNumberDouble();

        Chrome.FollowingListClick();
        WebElement fBody = Chrome.FocusFollowersList();
        Chrome.ScrollList(fBody, iamFollowing);
        List<WebElement> buttons = Chrome.GetButtonsFromList();
        List<WebElement> users = Chrome.GetUsernamesFromList();

        int i = 1;
        for (WebElement user : users) {
            if (Database.CheckIfUserExists(user.getText()) > 0)
            {
                if (Database.CheckDurationOfObservation(user.getText()) >= 72)
                {
                    buttons.get(i).click(); Bot.ActionPause(30);

                    Chrome.EndFollowingButtonClick();
                    SetInfoRemoveLabels();

                    Database.DeleteFromListOfFollowingInDb(user.getText());

                    EventLogger.DeleteObservationMessage(eventLogArea, user.getText()); Bot.ActionPause(30);
                }
            }
            i = i + 1;
        }
    }

    public void WorkWithPool() throws SQLException {
        Database.rs_pom = Database.GetUsersFromPool();

        int followsCounter = Database.CheckTodayFollows();

        while (Database.rs_pom.next() && followsCounter < 200)
        {

            String url = Database.rs_pom.getString("href");
            String user = Database.rs_pom.getString("nick");

            Chrome.GoToURL(url);

            Database.DeleteUserFromPool(user);
            SetInfoPoolLabel();

            int skipAccount = Database.HasAccountEverBeenVisited(user);

            if (skipAccount == 0)
            {
                try
                {
                    Chrome.driver.findElement(By.xpath("//*[@id='react-root']/section/main/div/div/article/div[1]/div/h2"));
                }
                catch(Exception e)
                {
                    try
                    {
                        int limit1 = Chrome.GetFollowersNumberInteger();
                        int limit2 = Chrome.GetFollowingNumberInteger();

                        if ((limit1 < 1000) && ((limit2 < 1000) && (limit2 > 100)))
                        {
                            try
                            {
                                Chrome.LikePhoto();
                                Database.AddStatFollowOrLike("like", user);
                                EventLogger.LikeUserMessage(eventLogArea, user);
                                SetInfoLikesLabel(); Bot.ActionPause(20);

                                Chrome.CloseWindowWithPhoto(); Bot.ActionPause(20);

                                Chrome.StartFollowingButtonClick();
                                Database.AddStatFollowOrLike("follow", user);
                                EventLogger.FollowUserMessage(eventLogArea, user);
                                SetInfoFollowsLabel();
                                SetInfoRemoveLabels(); Bot.ActionPause(20);
                            }
                            catch(Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    catch(Exception exx) {
                        System.out.println("exx");
                    }
                }
            }

            followsCounter = Database.CheckTodayFollows();
        }

        if (followsCounter >= 200)
        {
            EventLogger.EndFollowLikePoolMessage(eventLogArea, 1);
        }
        else
        {
            EventLogger.EmptyPoolMessage(eventLogArea);
        }

        Database.rs_pom.close();
        Database.rs.close();
    }

}
