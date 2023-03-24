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
import java.sql.DatabaseMetaData;
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
        table_name.setOnMouseClicked((MouseEvent event) -> {
            tablenameModel model = table_name.getItems()
                    .get(table_name.getSelectionModel().getSelectedIndex());
            tablename.setText(model.getTableName());
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

    void getdddl(String tableName) {
        try {

            // Connect to the database
            Connection conn = new dbConnection().java_connection();// replace with your connection details

            // Set auto-commit to false
            conn.setAutoCommit(false);

            // Get the table metadata
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet tableInfo = metadata.getTables(null, null, tableName, null);

            // Check if the table exists
            if (!tableInfo.next()) {
                System.out.println("Table " + tableName + " does not exist.");
                return;
            }

            // Get the column metadata for the table
            ResultSet columnInfo = metadata.getColumns(null, null, tableName, null);

            // Get the primary key metadata for the table
            ResultSet pkInfo = metadata.getPrimaryKeys(null, null, tableName);

            // Get the foreign key metadata for the table
            ResultSet fkInfo = metadata.getImportedKeys(null, null, tableName);

            // Generate the DDL for the table
            StringBuilder ddl = new StringBuilder();
            ddl.append("CREATE TABLE ").append(tableName).append(" (\n");

            // Add columns
            while (columnInfo.next()) {
                String columnName = columnInfo.getString("COLUMN_NAME");
                String columnType = columnInfo.getString("TYPE_NAME");
                int columnSize = columnInfo.getInt("COLUMN_SIZE");
                int decimalDigits = columnInfo.getInt("DECIMAL_DIGITS");
                int nullable = columnInfo.getInt("NULLABLE");
                 String autoIncrement = columnInfo.getString("IS_AUTOINCREMENT");

                ddl.append(columnName).append(" ").append(columnType).append("(")
                        .append(columnSize).append(",").append(decimalDigits).append(")");

                if (nullable == DatabaseMetaData.columnNoNulls) {
                    ddl.append(" NOT NULL");
                }
                if (autoIncrement.equalsIgnoreCase("YES")) {
                    ddl.append(" GENERATED ALWAYS AS IDENTITY");
                }

                ddl.append(", \n");
            }

            // Add primary key constraint
            StringBuilder pkConstraint = new StringBuilder();
            while (pkInfo.next()) {
                String pkColumnName = pkInfo.getString("COLUMN_NAME");
                pkConstraint.append(pkColumnName).append(",\n ");
            }
            if (pkConstraint.length() > 0) {
                pkConstraint.setLength(pkConstraint.length() - 2); // remove trailing comma
                ddl.append("PRIMARY KEY (").append(pkConstraint).append("), ");
            }

            // Add foreign key constraints
            while (fkInfo.next()) {
                String fkColumnName = fkInfo.getString("FKCOLUMN_NAME");
                String pkTableName = fkInfo.getString("PKTABLE_NAME");
                String pkColumnName = fkInfo.getString("PKCOLUMN_NAME");

                ddl.append("FOREIGN KEY (").append(fkColumnName).append(") REFERENCES ")
                        .append(pkTableName).append("(").append(pkColumnName).append("), ");
            }

            ddl.setLength(ddl.length() - 2); // remove trailing comma
            ddl.append(")");

            System.out.println(ddl.toString());
            viewddl.setText(ddl.toString());

            // Commit the transaction
            conn.commit();

            // Close the resources
            columnInfo.close();
            pkInfo.close();
            fkInfo.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();

        }
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
        try {
           // getdddl(tablename.getText().toUpperCase());
            
            
            
            Statement stmt = conn.createStatement();
            
            // Generate DDL statements for the table and its constraints
            String ddl = generateDDL(stmt, tablename.getText().toUpperCase());

            // Print the DDL statements
            System.out.println(ddl);

            // Close the statement and connection
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbSettingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 private static String generateDDL(Statement stmt, String tableName) throws SQLException {

        StringBuilder ddlBuilder = new StringBuilder();

        // Generate DDL for the table
        ResultSet rs = stmt.executeQuery("SELECT db2look('-t " + tableName + " -e -o -d WASHIDI -z MALIPLUS') FROM SYSIBM.SYSDUMMY1");
        if (rs.next()) {
            ddlBuilder.append(rs.getString(1));
        }

        // Generate DDL for the constraints
        rs = stmt.executeQuery("SELECT db2look('-d WASHIDI -z MALIPLUS -e -o -xref " + tableName + "') FROM SYSIBM.SYSDUMMY1");
        if (rs.next()) {
            ddlBuilder.append(rs.getString(1));
        }

        return ddlBuilder.toString();

    }
}
