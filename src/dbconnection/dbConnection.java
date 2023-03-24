/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbconnection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smartApps
 */
public class dbConnection {

    Properties properties = new Properties();
    InputStream inputStream;

    public Connection conn;

    String url, user, pass, database;

    public void loadPropertiseFile() {
        try {
            inputStream = new FileInputStream("setting.properties");
            properties.load(inputStream);
            url = "jdbc:db2://" + properties.getProperty("host") + ":" + properties.getProperty("port") + "/" + properties.getProperty("db");
            user = properties.getProperty("user");
            pass = properties.getProperty("password");

        } catch (IOException ex) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection java_connection() {
        loadPropertiseFile();
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection successful");
            return conn;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }
    
      public Connection rundbConnection() {
        loadPropertiseFile();
        try {
            String rnurl = "jdbc:db2://" + properties.getProperty("host") + ":" + properties.getProperty("port") + "/" + "HOTELDEM";
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            conn = DriverManager.getConnection(rnurl, "MALIPLUS", "3318");
            System.out.println("Connection successful");
            return conn;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
