package com.przemekwolczacki.instabot;

public final class Bot {

    public Bot(){
        Start();
    }

    public void Start(){
        LoginPanelFrame app = new LoginPanelFrame();
    }

    public static void DisplayUserPanel(){
        UserPanelFrame app = new UserPanelFrame();
    }
}
