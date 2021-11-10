package com.przemekwolczacki.instabot;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Database {

    public static Statement stmt = null;
    public static ResultSet rs = null;

    public static LocalDateTime now_date;
    public static DateTimeFormatter formatter;

    public static String information;

    public static int likesCounter;
    public static int followsCounter;
    public static int profilesCounter;

    public static int moreThan3Days = 0;
    public static int lessThan3Days = 0;

    public static int[] toReturn;

    public Database(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/instabot?user=root&password=");
            stmt = con.createStatement();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static int CountProfilesInPool() throws SQLException {
        rs = stmt.executeQuery("SELECT count(*) FROM pool");

        rs.next();
        profilesCounter = rs.getInt("count(*)");
        rs.close();

        return profilesCounter;
    }

    public static int[] CountObservationsToRemove() throws SQLException {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        now_date = LocalDateTime.now();

        moreThan3Days = 0;
        lessThan3Days = 0;

        rs = stmt.executeQuery("SELECT * FROM followed");

        while(rs.next()){
            LocalDateTime old_date = LocalDateTime.parse(rs.getString("date"), formatter);
            long diffInHours = Math.abs(java.time.Duration.between(now_date, old_date).toHours());

            if (diffInHours >= 72) moreThan3Days += 1;
            else lessThan3Days += 1;
        }

        rs.close();

        toReturn = new int[2];
        toReturn[0] = moreThan3Days + lessThan3Days;
        toReturn[1] = moreThan3Days;

        return toReturn;
    }

    public static void RemoveOtherDaysStats() throws SQLException {
        stmt.execute("DELETE FROM statistics WHERE dayAndMonth!='" + DayOfMonthFormatter() + "'");
    }

    public static int[] CheckTodayStats() throws SQLException {
        rs = stmt.executeQuery("SELECT count(*) FROM statistics WHERE actionType='follow'");
        rs.next();
        followsCounter = rs.getInt("count(*)");

        rs = stmt.executeQuery("SELECT count(*) FROM statistics WHERE actionType='like'");
        rs.next();
        likesCounter = rs.getInt("count(*)");

        rs.close();

        toReturn = new int[2];
        toReturn[0] = followsCounter;
        toReturn[1] = likesCounter;

        return toReturn;
    }

    public static void AddingToDbMessage(JTextArea eventLogArea){
        information = "[" + TimeOfDayFormatter() + "] Rozpoczęto dodawanie linków do profili w bazie\n";
        eventLogArea.append(information);
    }

    public static void EndOfAddingToDbMessage(JTextArea eventLogArea){
        information = "[" + TimeOfDayFormatter() + "] Zakończono dodawanie linków do profili w bazie \n";
        eventLogArea.append(information);
    }

    public static void ReachedUserLimitInDbMessage(JTextArea eventLogArea){
        eventLogArea.append("Limit użytkowników w bazie przekroczony \n");
    }

    public static void SomethingWentWrongMessage(JTextArea eventLogArea){
        eventLogArea.append("Coś poszło nie tak. Nie można dodać do puli \n");
    }

    public static void AddProfileToPool(String user, String href) throws SQLException {
        stmt.execute("INSERT INTO pool(nick, href) VALUES('" + user + "', '" + href + "')");
    }


    public static String TimeOfDayFormatter(){
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        now_date = LocalDateTime.now();

        return now_date.format(formatter);
    }

    public static String DayOfMonthFormatter(){
        formatter = DateTimeFormatter.ofPattern("MM-dd");
        now_date = LocalDateTime.now();

        return now_date.format(formatter);
    }


}
