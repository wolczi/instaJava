package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.JavascriptExecutor;

public final class Chrome {
    public static WebDriver driver;
    public static JavascriptExecutor js;

    public Chrome() throws InterruptedException {
        OpenBrowser();
    }

    private void OpenBrowser() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Przemek\\Documents\\Java Selenium\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        driver = new ChromeDriver();
        GoToURL("https://instagram.com/");
        driver.findElement(By.xpath("//*[text()='Akceptuję wszystko']")).click();

        js = (JavascriptExecutor) driver;
    }

    public static void GoToURL(String url) throws InterruptedException {
        driver.get(url);
        TimeUnit.SECONDS.sleep(2);
    }

    public static void LoginToInstagram(String username, String password) throws InterruptedException, SQLException {
        WebElement loginBox = driver.findElement(By.xpath("//input[@name='username']"));
        loginBox.click();
        loginBox.sendKeys(username);
        TimeUnit.SECONDS.sleep(2);

        WebElement passwordBox = driver.findElement(By.xpath("//input[@name='password']"));
        passwordBox.click();
        passwordBox.sendKeys(password);
        TimeUnit.SECONDS.sleep(2);

        driver.findElement(By.xpath("//button[contains(@type,'submit')]")).click();

        LoginVerification(loginBox, passwordBox);
    }

    private static void LoginVerification(WebElement loginBox, WebElement passwordBox) throws InterruptedException, SQLException {
        TimeUnit.SECONDS.sleep(10);

        if (driver.getCurrentUrl().equals("https://www.instagram.com/")) {
            JOptionPane.showMessageDialog(null, "Złe dane logowania!",
                    "Błąd logowania", JOptionPane.INFORMATION_MESSAGE);

            ClearLoginFieldsOnPage(loginBox, passwordBox);
        } else {
            LoginPanelFrame.Close();

            driver.findElement(By.xpath("//button[normalize-space()='Nie teraz']")).click();
            TimeUnit.SECONDS.sleep(3);
            driver.findElement(By.xpath("//*[text()='Nie teraz']")).click();

            Bot.DisplayUserPanel();
        }
    }

    private static void ClearLoginFieldsOnPage(WebElement loginBox, WebElement passwordBox) throws InterruptedException {
        while (!loginBox.getAttribute("value").equals("")) loginBox.sendKeys(Keys.BACK_SPACE);
        TimeUnit.SECONDS.sleep(2);
        while (!passwordBox.getAttribute("value").equals("")) passwordBox.sendKeys(Keys.BACK_SPACE);
    }

    public static double CountSomeonesFollowers() {
        return Math.ceil(Double.parseDouble(driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[2]/a/span")).getText()) / 12.0);
    }

    public static void DownloadSomeonesFollowers(JTextArea eventLogArea)  {
        try {
            double someonesFollowers = Chrome.CountSomeonesFollowers();

            driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[2]/a")).click();
            TimeUnit.SECONDS.sleep(3);

            var fBody = driver.findElement(By.xpath("//div[@class='isgrP']"));
            TimeUnit.SECONDS.sleep(3);

            double scroll = 0.0;
            while (scroll < someonesFollowers) {
                js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", fBody);
                TimeUnit.SECONDS.sleep(1);
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
}

