package com.przemekwolczacki.instabot;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws InterruptedException, SQLException {
        Chrome chrome = new Chrome();
        chrome.GoToInstagramPage();

        Bot bot = new Bot();
    }
}
