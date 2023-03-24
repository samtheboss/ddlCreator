/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package makedatabase;

import static com.ibm.db2.jcc.am.ap.Db;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import dbconnection.dbConnection;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

/**
 *
 * @author smartApps
 */
public class DbSettingController implements Initializable {

    String db2Path = "C:\\Program Files\\IBM\\SQLLIB\\BIN\\";
    Connection conn = new dbConnection().java_connection();
    PreparedStatement pst;
    ResultSet rs;
    @FXML
    private Button createtables;
    @FXML
    private Button dbseetings;
    Properties properties = new Properties();
    InputStream inputStream;
    Scanner scanner;
    @FXML
    private TextField tablename;
    @FXML
    private Button generateddl;
    @FXML
    private TextArea viewddl;
    @FXML
    private TextField schema;
    @FXML
    private TableView<tablenameModel> table_name;
    @FXML
    private TableColumn<?, ?> tabname;
    ObservableList<tablenameModel> tnames = FXCollections.observableArrayList();
    @FXML
    private JFXTextField tname;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filltoTextField();
        searchTable();
    }

    void filltoTextField() {
        tablenameModel Name = new tablenameModel();
        table_name.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tablenameModel model = table_name.getItems()
                        .get(table_name.getSelectionModel().getSelectedIndex());
                tablename.setText(model.getTableName());

            }
        });

    }

    public void getSchema(String schemaname) {
        try {
            inputStream = new FileInputStream("setting.properties");
            properties.load(inputStream);
            String username = properties.getProperty("user");
            

            tnames.clear();
            tabname.setCellValueFactory(new PropertyValueFactory<>("tableName"));

            String Schema = "SELECT tabname FROM SYSCAT.TABLES where tabschema ='" + schemaname + "'";
            pst = conn.prepareStatement(Schema);
            rs = pst.executeQuery();
            while (rs.next()) {
                schema.setText(rs.getString(1));
                tnames.add(new tablenameModel(rs.getString(1)));
                // System.out.println(rs.getString(1));

            }

        } catch (SQLException | IOException ex) {
            Logger.getLogger(DbSettingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        table_name.setItems(tnames);
    }

    void searchTable() {

        FilteredList<tablenameModel> filteredData = new FilteredList<>(tnames, p -> true);
        tname.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            filteredData.setPredicate(userdata -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (userdata.getTableName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (userdata.getTableName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<tablenameModel> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(table_name.comparatorProperty());

        table_name.setItems(sortedData);
    }

    public String getddl(final String tableName) {
        try {
            inputStream = new FileInputStream("setting.properties");
            properties.load(inputStream);
            String database = properties.getProperty("db");
            String username = properties.getProperty("user");
            String password = properties.getProperty("password");
            DDL clean = new DDL();
            clean.setTableName(tableName);

            String cmd = db2Path + "db2look -d " + database + " -e -t \"" + tableName + "\" -i " + username + " -w "
                    + password;
            System.out.println(cmd);
            scanner = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream());
            scanner = scanner.useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next() : "";
            clean.setDdl(result);
            clean.setSchama(username.toUpperCase());

            getSchema(clean.getSchama());
            clean.setSchama(username.toUpperCase() + ".");

            clean.cleanDDL();

            System.out.println(clean.getSchama());
            viewddl.clear();
            viewddl.setText(clean.getDdl());
            //  createDatabase(clean.getDdl());
            return result;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getLocalizedMessage());
            return "";
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
            return "";
        }
    }

    public void createDatabase(String ddl) {
        try {
            Connection rundb = new dbConnection().rundbConnection();
            Statement st = rundb.createStatement();
            st.execute(ddl);
//            Connection rundb = new dbConnection().rundbConnection();
//            pst = rundb.prepareStatement(ddl);
//            pst.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

    }

    @FXML
    private void createTables(ActionEvent event) {
        createDatabase(viewddl.getText());
        //  getddl(tablenameModel.getText());
    }

    @FXML
    private void dbseetings(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dbconnection/ConnectToMySql.fxml"));

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(DbSettingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void generateddl(ActionEvent event) {
        getddl(tablename.getText());
    }

}
