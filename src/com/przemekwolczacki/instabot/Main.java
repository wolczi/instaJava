package com.przemekwolczacki.instabot;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Chrome chrome = new Chrome();
        Bot bot = new Bot();

        chrome.GoToInstagramPage();
    }
}
