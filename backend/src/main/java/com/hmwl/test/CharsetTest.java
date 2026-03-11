package com.hmwl.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class CharsetTest {
    public static void main(String[] args) {
        try {
            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            String url = "jdbc:mysql://localhost:3306/hmwl?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
            String username = "root";
            String password = "Jxcwzk7226456!";
            Connection conn = DriverManager.getConnection(url, username, password);
            
            // Check database charset
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW VARIABLES LIKE 'character_set_%'");
            while (rs.next()) {
                System.out.println(rs.getString(1) + " = " + rs.getString(2));
            }
            
            // Check order table structure
            rs = stmt.executeQuery("SHOW CREATE TABLE `order`");
            while (rs.next()) {
                System.out.println("\nOrder table create statement:");
                System.out.println(rs.getString(2));
            }
            
            // Check order data
            rs = stmt.executeQuery("SELECT id, order_no, sender_name, receiver_name FROM `order` LIMIT 5");
            System.out.println("\nOrder data:");
            while (rs.next()) {
                System.out.println("id: " + rs.getInt(1) + ", order_no: " + rs.getString(2) + ", sender_name: " + rs.getString(3) + ", receiver_name: " + rs.getString(4));
            }
            
            // Close connection
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}