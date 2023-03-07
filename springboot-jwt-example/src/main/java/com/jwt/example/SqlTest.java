package com.jwt.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlTest {

    public static void main(String [] args) throws Exception {
        // Class.forName( "com.mysql.jdbc.Driver" ); // do this in init
        // // edit the jdbc url
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/jwt_db?user=root&password=password");
        // Statement st = conn.createStatement();
        // ResultSet rs = st.executeQuery( "select * from table" );

        System.out.println("Connected?");
    }
}