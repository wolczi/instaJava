package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.sql.SQLException;

import org.openqa.selenium.JavascriptExecutor;

public final class Chrome {
    public static WebDriver driver;
    public static JavascriptExecutor js;

    public Chrome() {
        OpenBrowser();
    }

    private void OpenBrowser() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Przemek\\Documents\\Java Selenium\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
    }

    public void GoToInstagramPage() {
        driver.get("https://instagram.com/");
        driver.findElement(By.xpath("//*[text()='Akceptuję wszystko']")).click();
        Bot.ActionPause(2);
    }

    public static void GoToURL(String url) {
        driver.get(url);
        Bot.ActionPause(2);
    }

    public static void LoginToInstagram(String username, String password) throws InterruptedException, SQLException {
        WebElement loginBox = driver.findElement(By.xpath("//input[@name='username']"));
        loginBox.click();
        loginBox.sendKeys(username); Bot.ActionPause(2);

        WebElement passwordBox = driver.findElement(By.xpath("//input[@name='password']"));
        passwordBox.click();
        passwordBox.sendKeys(password); Bot.ActionPause(2);

        driver.findElement(By.xpath("//button[contains(@type,'submit')]")).click();

        LoginVerification(loginBox, passwordBox);
    }

    private static void LoginVerification(WebElement loginBox, WebElement passwordBox) throws SQLException {
        Bot.ActionPause(5);

        if (driver.getCurrentUrl().equals("https://www.instagram.com/")) {
            JOptionPane.showMessageDialog(null, "Złe dane logowania!",
                    "Błąd logowania", JOptionPane.INFORMATION_MESSAGE);

            ClearLoginFieldsOnPage(loginBox, passwordBox);
        } else {
            LoginPanelFrame.Close();

            driver.findElement(By.xpath("//button[normalize-space()='Nie teraz']")).click();
            Bot.ActionPause(3);
            driver.findElement(By.xpath("//*[text()='Nie teraz']")).click();

            Bot.DisplayUserPanel();
        }
    }

    private static void ClearLoginFieldsOnPage(WebElement loginBox, WebElement passwordBox) {
        while (!loginBox.getAttribute("value").equals("")) loginBox.sendKeys(Keys.BACK_SPACE);
        Bot.ActionPause(2);
        while (!passwordBox.getAttribute("value").equals("")) passwordBox.sendKeys(Keys.BACK_SPACE);
    }

    public static double CountSomeonesFollowers() {
        return Math.ceil(Double.parseDouble(driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[2]/a/span")).getText()) / 12.0);
    }

    public static double CountHowManyPeopleIamFollowing() {
        return Math.ceil(Double.parseDouble(driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[3]/a/span")).getText()) / 12.0);
    }

    public static void DownloadSomeonesFollowers(JTextArea eventLogArea)  {
        try {
            double someonesFollowers = Chrome.CountSomeonesFollowers();

            driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[2]/a")).click();
            Bot.ActionPause(3);

            var fBody = driver.findElement(By.xpath("//div[@class='isgrP']"));
            Bot.ActionPause(3);

            double scroll = 0.0;
            while (scroll < someonesFollowers) {
                js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", fBody);
                Bot.ActionPause(5);
                scroll += 1.0;
            }

            var buttons = driver.findElements(By.className("sqdOP"));
            var users = driver.findElements(By.className("FPmhX"));

            int i = 0;
            for (WebElement user : users) {
                try {
                    if (Database.CountProfilesInPool() < 1000) {
                        if (buttons.get(i).getText().equals("Obserwuj")) {
                            Database.AddProfileToPool(user.getText(), user.getAttribute("href"));
                        }
                    } else {
                        Database.ReachedUserLimitInDbMessage(eventLogArea);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                i = i + 1;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Database.SomethingWentWrongMessage(eventLogArea);
        }
    }

    public static void DeleteTooLongObservations(JTextArea eventLogArea) throws SQLException {
        Bot.ActionPause(3);

        Database.StartDeleteObservationsMessage(eventLogArea);

        driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[3]/a")).click();
        double iamFollowing = CountHowManyPeopleIamFollowing();
        Bot.ActionPause(3);

        var fBody = driver.findElement(By.xpath("//div[@class='isgrP']"));

        double scroll = 0.0;
        while (scroll < iamFollowing)
        {
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", fBody);
            Bot.ActionPause(1);
            scroll += 1.0;
        }

        var buttons = driver.findElements(By.className("sqdOP"));
        var users = driver.findElements(By.className("FPmhX"));

        int i = 1;
        for (WebElement user : users) {
            if (Database.CheckIfUserExists(user.getText()) > 0)
            {
                if (Database.CheckDurationOfObservation(user.getText()) >= 72)
                {
                    buttons.get(i).click();
                    Bot.ActionPause(5);

                    driver.findElement(By.xpath("//button[contains(text(),'Przestań obserwować')]")).click();

                    Database.DeleteFromListOfFollowingInDb(user.getText());

                    eventLogArea.append("Przestano obserwować " + user.getText() + "\n");
                    Bot.ActionPause(5);
                }
            }
            i = i + 1;
        }

        Database.EndDeleteObservationsMessage(eventLogArea);
    }
}


