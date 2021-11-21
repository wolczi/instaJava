package com.przemekwolczacki.instabot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateModeler {
    private static LocalDateTime now_date;
    private static DateTimeFormatter formatter;

    public static LocalDateTime GetFullDate(String date){
        formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(date, formatter);
    }

    public static String GetTimeOfDay(){
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        now_date = LocalDateTime.now();

        return now_date.format(formatter);
    }

    public static String GetDayAndMonth(){
        formatter = DateTimeFormatter.ofPattern("MM-dd");
        now_date = LocalDateTime.now();

        return now_date.format(formatter);
    }
}
