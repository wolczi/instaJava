package com.przemekwolczacki.instabot;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Database {

    public static Statement stmt = null;
    public static ResultSet rs = null;

    public Database(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/instabot?user=root&password=");
            stmt = con.createStatement();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        } // obsluga bledow
    }

    public static int countProfilesInPool() throws SQLException {
        rs = stmt.executeQuery("SELECT count(*) FROM pool");

        rs.next();
        int count = rs.getInt("count(*)");
        //System.out.println("Table contains "+ count +" rows");
        rs.close();

        return count;
    }

    public static int[] countObservationsToRemove() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now_date = LocalDateTime.now();

        int moreThan3Days = 0;
        int lessThan3Days = 0;

        rs = stmt.executeQuery("SELECT * FROM followed");
        while(rs.next()){
            LocalDateTime old_date = LocalDateTime.parse(rs.getString("date"), formatter);
            long diffInHours = Math.abs(java.time.Duration.between(now_date, old_date).toHours());
            //System.out.println(diffInHours);

            if (diffInHours >= 72) moreThan3Days += 1;
            else lessThan3Days += 1;

        }
        rs.close();

        int[] toReturn = new int[2];

        toReturn[0] = moreThan3Days + lessThan3Days;
        toReturn[1] = moreThan3Days;

        return toReturn;
    }

}
