package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

final class Chrome{

    public static WebDriver driver;

    public Chrome() {
        openBrowser();
    }

    public void goToInstagramPage() throws InterruptedException {
        driver.get("https://instagram.com/");
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.xpath("//*[text()='Akceptuję wszystko']")).click();
    }

    public static void loginToInstagram(String username, String password){
        WebElement loginBox = driver.findElement(By.xpath("//input[@name='username']"));
        loginBox.click();
        loginBox.sendKeys(username);

        WebElement passwordBox = driver.findElement(By.xpath("//input[@name='password']"));
        passwordBox.click();
        passwordBox.sendKeys(password);
    }

    private void openBrowser(){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Przemek\\Documents\\Java Selenium\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");

        driver = new ChromeDriver();
        //driver.manage().window().maximize();
    }

}

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Chrome chrome = new Chrome();
        chrome.goToInstagramPage();

        Login loginFrame = new Login();
    }
}
