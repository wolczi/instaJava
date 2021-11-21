package com.przemekwolczacki.instabot;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;

public final class Database {

    public static Statement stmt = null;
    public static Statement stmt_pom = null;
    public static ResultSet rs = null;
    public static ResultSet rs_pom = null;

    public static int likesCounter;
    public static int followsCounter;
    public static int profilesCounter;

    public static int moreThan3Days = 0;
    public static int lessThan3Days = 0;
    public static long diffInHours;

    public Database(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/instabot?user=root&password=");
            Connection con_pom = DriverManager.getConnection("jdbc:mysql://localhost/instabot?user=root&password=");
            stmt = con.createStatement();
            stmt_pom = con_pom.createStatement();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Problem z połączeniem do bazy danych!",
                    "Błąd bazy danych", JOptionPane.INFORMATION_MESSAGE);
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
        LocalDateTime now_date = LocalDateTime.now();

        moreThan3Days = 0;
        lessThan3Days = 0;

        rs = stmt.executeQuery("SELECT * FROM followed");

        while(rs.next()){
            LocalDateTime old_date = DateModeler.GetFullDate(rs.getString("date"));
            diffInHours = Math.abs(java.time.Duration.between(now_date, old_date).toHours());

            if (diffInHours >= 72) moreThan3Days += 1;
            else lessThan3Days += 1;
        }

        rs.close();

        int[] counters = new int[2];
        counters[0] = moreThan3Days + lessThan3Days;
        counters[1] = moreThan3Days;

        return counters;
    }

    public static void RemoveOtherDaysStats() throws SQLException {
        stmt.execute("DELETE FROM statistics WHERE dayAndMonth!='" + DateModeler.GetDayAndMonth() + "'");
    }

    public static int CheckTodayLikes() throws SQLException {
        rs = stmt.executeQuery("SELECT count(*) FROM statistics WHERE actionType='like'");
        rs.next();
        likesCounter = rs.getInt("count(*)");

        rs.close();

        return likesCounter;
    }

    public static int CheckTodayFollows() throws SQLException {
        rs = stmt.executeQuery("SELECT count(*) FROM statistics WHERE actionType='follow'");
        rs.next();
        followsCounter = rs.getInt("count(*)");

        rs.close();

        return followsCounter;
    }

    public static void AddProfileToPool(String user, String href) throws SQLException {
        stmt.execute("INSERT INTO pool(nick, href) VALUES('" + user + "', '" + href + "')");
    }

    public static void AddStatFollowOrLike(String actionType, String nick) throws SQLException {
        stmt.execute("INSERT INTO statistics(id, user, actionType, dayAndMonth) VALUES(NULL, '" +
                nick + "', '" + actionType + "', '" + DateModeler.GetDayAndMonth() + "')");
    }

    public static void DeleteFromListOfFollowingInDb(String user) throws SQLException {
        stmt.execute("DELETE FROM followed WHERE nick='" + user + "'");
    }

    public static int CheckIfUserExists(String user) throws SQLException {
        rs = stmt.executeQuery("SELECT count(1) FROM followed WHERE nick='" + user + "'");

        rs.next();
        profilesCounter = rs.getInt("count(1)");
        rs.close();

        return profilesCounter;
    }

    public static long CheckDurationOfObservation(String user) throws SQLException {
        rs = stmt.executeQuery("SELECT * FROM followed WHERE nick='" + user + "'");

        rs.next();

        LocalDateTime now_date = LocalDateTime.now();
        LocalDateTime old_date = DateModeler.GetFullDate(rs.getString("date"));
        diffInHours = Math.abs(java.time.Duration.between(now_date, old_date).toHours());

        rs.close();

        return diffInHours;
    }

}
