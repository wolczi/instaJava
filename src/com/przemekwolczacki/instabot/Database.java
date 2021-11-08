package com.przemekwolczacki.instabot;

import java.sql.*;

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

    public static int checkPoolSize() throws SQLException {
        rs = stmt.executeQuery("SELECT count(*) FROM pool");

        rs.next();
        int count = rs.getInt("count(*)");
        System.out.println("Table contains "+ count +" rows");
        rs.close();

        return count;
    }

}
