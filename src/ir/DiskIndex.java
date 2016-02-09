package ir;

import java.sql.*;

/**
 * Created by swebo_000 on 2016-02-07.
 */
public class DiskIndex {

    private Connection c;
    private int chunk = 200000;
    private int counter = 0;
    private int sum = 0;

    PreparedStatement pStmt;
    String sqlInsert = "INSERT INTO DavisIndex (token, docID, offset)" +
            "VALUES(?,?,?)";


    public DiskIndex() {
        //TODO: Setup DB
    }

    public void createDB(String name) {
        if (!existsTable()) {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:" + name); //Creates the DB if it doesn't exist.
                createTable();
            } catch (ClassNotFoundException e) {
                System.err.println("No class driver found!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("SQL error!");
                e.printStackTrace();
            }
        } else {
            System.out.println("The DB already exists. ");
        }

    }

    public boolean existsTable() {
        String sqlQuery = "SELECT name FROM sqlite_master WHERE name='DavisIndex'";
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            if (rs.wasNull()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void createTable() {
        Statement stmt;
        String table = "CREATE TABLE DavisIndex (" +
                "token TEXT NOT NULL," +
                "docID INT NOT NULL," +
                "offset INT NOT NULL" +
                ")";
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(table);
            stmt.close();
            pStmt = c.prepareStatement(sqlInsert);

            //c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Table successfully created.");

    }

    public void insert(String token, int docID, int offset) {

        try {
            pStmt.getConnection().setAutoCommit(false);
           // if(counter != chunk) {
                counter++;
                pStmt.setString(1, token);
                pStmt.setInt(2, docID);
                pStmt.setInt(3, offset);
                pStmt.executeUpdate();
                pStmt.clearParameters();
            if (counter % 100000 == 0) {
                System.out.println("Current records: " + counter);
            }
            // }
             /*else {
                c.commit();
                c.setAutoCommit(true);
                sum += counter;
                counter = 0;
                System.out.println("Transaction made!");
                System.out.println("Total entries = " + sum);
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commitTransaction() {
        try {
            System.out.println("Transaction committed.");
            System.out.println("Total records: " + counter);
            c.commit();
            c.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

