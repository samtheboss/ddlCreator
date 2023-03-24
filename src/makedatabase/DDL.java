package makedatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DDL {

    private String tableName;
    private String ddl;

    String schama;

    public DDL(String tableName, String ddl, String schama) {
        super();
        this.tableName = tableName;
        this.ddl = ddl;
        this.schama = schama;
    }

// UpdaterController.SCHEMA + ".";
//    public DDL(final String tableName, final String ddl)
//    {
//        super();
//
//        this.tableName = tableName;
//        this.ddl = ddl;
//    }
//
    public DDL() {
        this.tableName = "";
        this.ddl = "";
        this.schama = "";
    }

    public String getSchama() {
        return schama;
    }

    public void setSchama(String schama) {
        this.schama = schama;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public void cleanDDL() {
        if (!ddl.isEmpty()) {
            String tempDDL = ddl;
            String[] arrangedString = tempDDL.split("\n");

            List<String> lstArranged = new ArrayList<>(Arrays.asList(arrangedString));

            List<String> lstWithoutComments = new ArrayList<>();
            lstArranged.forEach(str -> {
                if (!str.startsWith("--")
                        && !str.toUpperCase().startsWith("CONNECT ")
                        && !str.toUpperCase().startsWith("SET ")
                        && !str.toUpperCase().startsWith("CREATE SCHEMA")
                        && !str.toUpperCase().startsWith("TERMINATE")
                        && str.trim().length() > 0) {
                    lstWithoutComments.add(str);
                }
            });

            List<String> lstWithoutQuotes = new ArrayList<>();
            lstWithoutComments.forEach(str -> {
                if (!str.trim().toUpperCase().startsWith("ADD CONSTRAINT")) {
                    String withoutQuotes = str.replace("\"", "");
                    lstWithoutQuotes.add(withoutQuotes);

                } else {
                    lstWithoutQuotes.add(str);
                }
            });

            List<String> getShema = new ArrayList<>();
            getShema.forEach(str -> {
                if (!str.trim().toUpperCase().startsWith(schama)) {
                    String getShemas = str.replace(str, "");
                    getShema.add(getShemas);
                } else {
                    lstWithoutQuotes.add(str);
                }
            });

            String message = " for Table " + tableName.toUpperCase();
            if (tableName.equalsIgnoreCase("FULL")) {
                message = "";
            }

            ddl = "------------------------------------------------\n"
                    + "-- DDL Statements " + message + "\n"
                    + "------------------------------------------------";
            lstWithoutQuotes.forEach(itm -> ddl += "\n" + itm);

            ddl = ddl.replace(schama, "");

        }
    }

    @Override
    public String toString() {
        return "DDL [tableName=" + tableName + ", ddl=" + ddl + "]";
    }
}
