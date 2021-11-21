package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;


public final class Chrome {
    public static WebDriver driver;
    public static JavascriptExecutor js;

    public Chrome() throws InterruptedException {
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
        Bot.ActionPause(3);
        driver.findElement(By.xpath("//*[text()='Akceptuję wszystko']")).click();
    }

    public static void GoToURL(String url) {
        driver.get(url);
        Bot.ActionPause(2);
    }

    public static void ClearLoginFieldsOnPage(WebElement loginBox, WebElement passwordBox) {
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

}


