package com.przemekwolczacki.instabot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;

import java.util.List;


public final class Chrome {
    public static WebDriver driver;
    public static JavascriptExecutor js;

    public Chrome() {
        OpenBrowser();
    }

    private void OpenBrowser() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
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

    public static double GetFollowersNumberDouble() {
        return Math.ceil(Double.parseDouble(driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[2]/a/span")).getText()) / 12.0);
    }

    public static double GetFollowingNumberDouble() {
        return Math.ceil(Double.parseDouble(driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[3]/a/span")).getText()) / 12.0);
    }

    public static int GetFollowersNumberInteger() {
        String number1 = Chrome.driver.findElement(By.xpath("//*[@id='react-root']/section/main/div/header/section/ul/li[2]/a/span")).getAttribute("title");
        number1 = number1.replaceAll("\\s+","");

        return Integer.parseInt(number1);
    }

    public static int GetFollowingNumberInteger() {
        String number2 = Chrome.driver.findElement(By.xpath("//*[@id='react-root']/section/main/div/header/section/ul/li[3]/a/span")).getText();
        number2 = number2.replaceAll("\\s+","");

        return Integer.parseInt(number2);
    }

    public static void LikePhoto() {
        driver.findElement(By.className("v1Nh3")).click(); Bot.ActionPause(20);
        driver.findElement(By.xpath("//span[contains(@class,'fr66n')]//button[contains(@type,'button')]")).click();
    }

    public static void CloseWindowWithPhoto() {
        driver.findElement(By.xpath("/html/body/div[6]/div[3]/button")).click();
    }

    public static void ScrollList(WebElement fBody, double limit) {
        double scroll = 0.0;
        while (scroll < limit) {
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", fBody);
            Bot.ActionPause(5);
            scroll += 1.0;
        }
    }

    public static void FollowersListClick() {
        driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[2]/a")).click();
        Bot.ActionPause(3);
    }

    public static void FollowingListClick() {
        driver.findElement(By.xpath("/html/body/div[1]/section/main/div/header/section/ul/li[3]/a")).click();
        Bot.ActionPause(3);
    }

    public static void EndFollowingButtonClick() {
        driver.findElement(By.xpath("//button[contains(text(),'Przestań obserwować')]")).click();
    }

    public static void StartFollowingButtonClick() {
        driver.findElement(By.xpath("//button[normalize-space()='Obserwuj']")).click();
    }

    public static WebElement FocusFollowersList() {
        WebElement webElement = driver.findElement(By.xpath("//div[@class='isgrP']"));
        Bot.ActionPause(3);

        return webElement;
    }

    public static List<WebElement> GetButtonsFromList() {
        return driver.findElements(By.className("sqdOP"));
    }

    public static List<WebElement> GetUsernamesFromList() {
        return driver.findElements(By.className("FPmhX"));
    }


}


