/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbconnection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author smartApps
 */
public class serverSettings implements Initializable {

    @FXML
    private TextField tfHostName;
    @FXML
    private TextField tfPortName;
    @FXML
    private TextField tfUserName;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Button btnSave;
    Properties properties = new Properties();
    InputStream inputStream;
    OutputStream outputStream = null;
    Connection conn;
    dbConnection dbconn = new dbConnection();

    String url, user, pass, db;
    String unicode = "?useUnicode=yes&characterEncoding=UTF-8";
    @FXML
    private TextField tfDBName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getdabaseSettings();
    }

    @FXML
    private void btnSaveOnAction(ActionEvent event) {
        setDatabasesetting();
    }

    public void setDatabasesetting() {
        try {
            outputStream = new FileOutputStream("setting.properties");
            properties.setProperty("host", tfHostName.getText().trim());
            properties.setProperty("port", tfPortName.getText().trim());
            properties.setProperty("password", pfPassword.getText());
            properties.setProperty("user", tfUserName.getText());
            properties.setProperty("db", tfDBName.getText().trim());
            properties.store(outputStream, null);
            outputStream.close();
              conn = dbconn.java_connection();

        } catch (IOException ex) {
            Logger.getLogger(serverSettings.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void getdabaseSettings() {
        try {
            inputStream = new FileInputStream("setting.properties");
            properties.load(inputStream);
            tfHostName.setText(properties.getProperty("host"));
            tfPortName.setText(properties.getProperty("port"));
            tfUserName.setText(properties.getProperty("user"));
            pfPassword.setText(properties.getProperty("password"));
            tfDBName.setText(properties.getProperty("db"));
            inputStream.close();

        } catch (IOException ex) {
            Logger.getLogger(serverSettings.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
   
}
