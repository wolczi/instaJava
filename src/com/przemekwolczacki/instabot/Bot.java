package com.przemekwolczacki.instabot;

import java.sql.SQLException;

public final class Bot {

    public Bot() throws InterruptedException {
        Start();
    }

    public void Start() throws InterruptedException {
        Chrome chrome = new Chrome();
        LoginPanelFrame app = new LoginPanelFrame();
    }

    public static void DisplayUserPanel() throws SQLException {
        UserPanelFrame app = new UserPanelFrame();
    }
}
