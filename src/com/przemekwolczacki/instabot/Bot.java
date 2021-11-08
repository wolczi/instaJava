package com.przemekwolczacki.instabot;

import java.sql.SQLException;

public final class Bot {

    public Bot() {
        Start();
    }

    public void Start(){
        LoginPanelFrame app = new LoginPanelFrame();
    }

    public static void DisplayUserPanel() throws SQLException {
        UserPanelFrame app = new UserPanelFrame();
    }
}
