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

public final class Chrome {
    public static WebDriver driver;

    public Chrome() {
        OpenBrowser();
    }

    private void OpenBrowser(){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Przemek\\Documents\\Java Selenium\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");

        driver = new ChromeDriver();
    }

    public void GoToInstagramPage() throws InterruptedException {
        driver.get("https://instagram.com/"); TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.xpath("//*[text()='Akceptuję wszystko']")).click();
    }

    public static void LoginToInstagram(String username, String password) throws InterruptedException, SQLException {
        WebElement loginBox = driver.findElement(By.xpath("//input[@name='username']"));
        loginBox.click();
        loginBox.sendKeys(username); TimeUnit.SECONDS.sleep(2);

        WebElement passwordBox = driver.findElement(By.xpath("//input[@name='password']"));
        passwordBox.click();
        passwordBox.sendKeys(password); TimeUnit.SECONDS.sleep(2);

        driver.findElement(By.xpath("//button[contains(@type,'submit')]")).click();

        LoginVerification(loginBox, passwordBox);
    }

    private static void LoginVerification(WebElement loginBox, WebElement passwordBox) throws InterruptedException, SQLException {
        TimeUnit.SECONDS.sleep(10);

        if (driver.getCurrentUrl().equals("https://www.instagram.com/"))
        {
            JOptionPane.showMessageDialog(null, "Złe dane logowania!",
                    "Błąd logowania", JOptionPane.INFORMATION_MESSAGE);

            ClearLoginFieldsOnPage(loginBox, passwordBox);
        }
        else
        {
            LoginPanelFrame.Close();

            driver.findElement(By.xpath("//button[normalize-space()='Nie teraz']")).click(); TimeUnit.SECONDS.sleep(3);
            driver.findElement(By.xpath("//*[text()='Nie teraz']")).click();

            Bot.DisplayUserPanel();
        }
    }

    private static void ClearLoginFieldsOnPage(WebElement loginBox, WebElement passwordBox) throws InterruptedException {
        while (!loginBox.getAttribute("value").equals("")) loginBox.sendKeys(Keys.BACK_SPACE); TimeUnit.SECONDS.sleep(2);
        while (!passwordBox.getAttribute("value").equals("")) passwordBox.sendKeys(Keys.BACK_SPACE);
    }
}
