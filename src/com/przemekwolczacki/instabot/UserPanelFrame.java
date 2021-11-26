package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    int[] statCounters;
    int followsCounter;

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
                        EventLogger.AddingToDbMessage(eventLogArea);

                        DownloadSomeonesFollowers();
                        SetInfoPoolLabel();
                    } catch (InterruptedException | SQLException ex) {
                        ex.printStackTrace();
                    }
                    EventLogger.EndOfAddingToDbMessage(eventLogArea);

                    return 1;
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
                        }
                        else {
                            EventLogger.NoOneToRemoveMessage(eventLogArea);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    EventLogger.EndDeleteObservationsMessage(eventLogArea);

                    return 1;
                }
            };

            sw2.execute();
        });
        workWithAccountsPoolButton.addActionListener(e -> {
            sw3 = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    try {
                        EventLogger.StartFollowLikePoolMessage(eventLogArea);

                        int followsCounter = Database.CheckTodayFollows();

                        if (followsCounter < 200) {
                            if (Database.CountProfilesInPool() > 0) WorkWithPool();
                            else EventLogger.EndFollowLikePoolMessage(eventLogArea, 0);
                        }
                        else
                        {
                            EventLogger.EndFollowLikePoolMessage(eventLogArea, 1);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    EventLogger.EndFollowLikePoolMessage(eventLogArea, 2);

                    return 1;
                }
            };

            sw3.execute();
        });

        stopButton.addActionListener(e -> {
            try {
                sw1.cancel(true);
                sw1 = null;
                SetInfoPoolLabel();
                EventLogger.CancelAddToPoolMessage(eventLogArea);
            } catch(Exception c){}

            try {
                sw2.cancel(true);
                sw2 = null;
                SetInfoRemoveLabels();
                EventLogger.CancelRemoveFollowsMessage(eventLogArea);
            } catch(Exception c){}

            try {
                sw3.cancel(true);
                sw3 = null;
                SetInfoFollowsLabel();
                SetInfoLikesLabel();
                EventLogger.CancelWorkWithPoolMessage(eventLogArea);
            } catch(Exception c){}
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
        statCounters = Database.CountObservationsToRemove();
        infoRemoveLabel2.setText("ogólnie: " + statCounters[0]);
        infoRemoveLabel3.setText("dłużej niż trzy dni: " + statCounters[1]);

        infoRemoveLabel2.paintImmediately(infoRemoveLabel2.getVisibleRect());
        infoRemoveLabel3.paintImmediately(infoRemoveLabel3.getVisibleRect());
    }

    public void SetInfoPoolLabel() throws SQLException {
        infoPoolLabel.setText("Liczba kont w bazie (nieodwiedzonych): " + Database.CountProfilesInPool());

        infoPoolLabel.paintImmediately(infoPoolLabel.getVisibleRect());
    }

    /*

    -
    -
    -
    -
    -

     */

    public void DownloadSomeonesFollowers() throws InterruptedException, SQLException {
        url = linkToProfileField.getText();

        Chrome.GoToURL(url);

        double someonesFollowers = Chrome.CountSomeonesFollowers();

        Chrome.driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[2]/a")).click();
        Bot.ActionPause(3);

        var fBody = Chrome.driver.findElement(By.xpath("//div[@class='isgrP']"));
        Bot.ActionPause(3);

        double scroll = 0.0;
        while (scroll < someonesFollowers) {
            Chrome.js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", fBody);
            Bot.ActionPause(5);
            scroll += 1.0;
        }

        var buttons = Chrome.driver.findElements(By.className("sqdOP"));
        var users = Chrome.driver.findElements(By.className("FPmhX"));

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

        Bot.ActionPause(3);

        Chrome.driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[3]/a")).click();
        double iamFollowing = Chrome.CountHowManyPeopleIamFollowing();
        Bot.ActionPause(3);

        var fBody = Chrome.driver.findElement(By.xpath("//div[@class='isgrP']"));

        double scroll = 0.0;
        while (scroll < iamFollowing)
        {
            Chrome.js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", fBody);
            Bot.ActionPause(1);
            scroll += 1.0;
        }

        var buttons = Chrome.driver.findElements(By.className("sqdOP"));
        var users = Chrome.driver.findElements(By.className("FPmhX"));

        int i = 1;
        for (WebElement user : users) {
            if (Database.CheckIfUserExists(user.getText()) > 0)
            {
                if (Database.CheckDurationOfObservation(user.getText()) >= 72)
                {
                    buttons.get(i).click();
                    Bot.ActionPause(5);

                    Chrome.driver.findElement(By.xpath("//button[contains(text(),'Przestań obserwować')]")).click();

                    Database.DeleteFromListOfFollowingInDb(user.getText());

                    eventLogArea.append("[" + DateModeler.GetTimeOfDay() + "] Przestano obserwować " + user.getText() + "\n");
                    Bot.ActionPause(5);
                }
            }
            i = i + 1;
        }
    }

    public void WorkWithPool() throws SQLException {
        Database.rs_pom = Database.stmt_pom.executeQuery("SELECT * FROM pool");

        followsCounter = Database.CheckTodayFollows();

        while (Database.rs_pom.next() && followsCounter < 200)
        {

            String url = Database.rs_pom.getString("href");

            Chrome.GoToURL(url); Bot.ActionPause(3);

            String user = Database.rs_pom.getString("nick");

            Database.stmt.execute( "DELETE FROM pool WHERE nick='" + user + "'");
            SetInfoPoolLabel();

            Database.rs = Database.stmt.executeQuery("SELECT count(1) FROM dontfollow WHERE nick='" + user + "'");
            Database.rs.next();
            int donotfollow = Database.rs.getInt("count(1)");

            if (donotfollow == 0)
            {
                try
                {
                    Chrome.driver.findElement(By.xpath("//*[@id='react-root']/section/main/div/div/article/div[1]/div/h2"));
                }
                catch(Exception e)
                {
                    try
                    {
                        String check1 = Chrome.driver.findElement(By.xpath("//*[@id='react-root']/section/main/div/header/section/ul/li[2]/a/span")).getAttribute("title");
                        check1 = check1.replaceAll("\\s+","");

                        String check2 = Chrome.driver.findElement(By.xpath("//*[@id='react-root']/section/main/div/header/section/ul/li[3]/a/span")).getText();
                        check2 = check2.replaceAll("\\s+","");

                        if ((Integer.parseInt(check1) < 1000) && ((Integer.parseInt(check2) < 1000) && (Integer.parseInt(check2) > 100)))
                        {
                            try
                            {
                                Chrome.driver.findElement(By.className("v1Nh3")).click();
                                Bot.ActionPause(20);

                                Chrome.driver.findElement(By.xpath("//span[contains(@class,'fr66n')]//button[contains(@type,'button')]")).click();
                                Database.AddStatFollowOrLike("like", user);
                                EventLogger.LikeUserMessage(eventLogArea, user);
                                SetInfoLikesLabel();
                                Bot.ActionPause(20);

                                Chrome.driver.findElement(By.xpath("/html/body/div[6]/div[3]/button")).click();
                                Bot.ActionPause(20);

                                Chrome.driver.findElement(By.xpath("//button[normalize-space()='Obserwuj']")).click();
                                Database.stmt.execute("INSERT INTO followed(nick, date) VALUES('" + user + "', '" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "')");
                                Database.stmt.execute("INSERT INTO dontfollow(id, nick) VALUES(NULL, '" + user + "')");
                                Database.AddStatFollowOrLike("follow", user);
                                EventLogger.FollowUserMessage(eventLogArea, user);
                                SetInfoFollowsLabel();
                                SetInfoRemoveLabels();

                                Bot.ActionPause(20);
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
