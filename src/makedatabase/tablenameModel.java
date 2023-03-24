/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package makedatabase;

/**
 *
 * @author smartApps
 */
public class tablenameModel {
    String tableName;

    public tablenameModel() {
    }
    

    public tablenameModel(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
