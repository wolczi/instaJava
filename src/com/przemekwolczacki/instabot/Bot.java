package com.przemekwolczacki.instabot;

import org.openqa.selenium.interactions.Actions;

import java.sql.SQLException;
import java.time.Duration;

public final class Bot {
    public static Actions action;

    public Bot() {
        Start();
    }

    public void Start() {
        action = new Actions(Chrome.driver);
        LoginPanelFrame app = new LoginPanelFrame();
    }

    public static void DisplayUserPanel() throws SQLException {
        UserPanelFrame app = new UserPanelFrame();
    }

    public static void ActionPause(int seconds) {
        Bot.action.pause(Duration.ofSeconds(seconds)).perform();
    }
}
