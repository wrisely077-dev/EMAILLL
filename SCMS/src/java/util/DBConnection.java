package util;

import java.sql.*;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:oracle:thin:@localhost:1521:xe"; 
        String user = "SYSTEM";         // Change this to your actual username
        String pass = "phainon";    // Change this to your actual password
        
        System.out.println("DEBUG: Attempting connection to Oracle DB");
        System.out.println("DEBUG: URL: " + url);
        System.out.println("DEBUG: User: " + user);
        
        return DriverManager.getConnection(url, user, pass);
    }
}