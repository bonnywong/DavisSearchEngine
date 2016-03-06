package ir;

import java.sql.*;

/**
 * Created by swebo_000 on 2016-02-07.
 */
public class DiskIndex {

    private Connection conn;
    private int chunk = 20000;
    private int counter = 0;
    private int sum = 0;

    PreparedStatement pStmt;
    PreparedStatement existStmt;
    PreparedStatement insertStmt;
    PreparedStatement updateStmt;
    public DiskIndex() {
    }

    public boolean connectDB(String name) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + name); //Creates the DB if it doesn't exist.
            createTable();
            conn.setAutoCommit(false);
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("No class driver found!");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("SQL error!");
            e.printStackTrace();
            return false;
        }
    }

    /***
     * Checks if the DavisIndex table exists in the database.
     *
     * @return
     */
    public boolean existsTable() {
        String sqlQuery = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='DavisIndex'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
                if (rs.getInt(1) > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createTable() {

        if (!existsTable()) {
            Statement stmt;
            /*
            String table = "CREATE TABLE DavisIndex (" +
                    "token TEXT NOT NULL," +
                    "docID INT NOT NULL," +
                    "offset INT NOT NULL" +
                    ")";
            */
            String table = "CREATE TABLE DavisIndex (" +
                    "token TEXT NOT NULL," +
                    "docID INT NOT NULL," +
                    "offset TEXT NOT NULL" +
                    ")";
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(table);
                stmt.close();

                //Used during insertion.
                //pStmt = conn.prepareStatement(sqlInsert);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("DB table successfully created.");
        } else {
            System.out.println("DB table exists.");
        }

    }

    public void createSQLindex() {
        String index = "CREATE INDEX token ON DavisIndex(token)";
        Statement stmt;

        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(index);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("SQL index creation failed.");
            e.printStackTrace();
        }
        System.out.println("SQL index created.");
    }


    /**
     * Checks if the specified token and associated docID exists.
     *
     * @param token The token
     * @param docID The documentID which the token is found in
     * @return The offsets
     */
    public String existsToken(String token, int docID){
        String query = "SELECT * FROM DavisIndex WHERE token = ? AND docID = ?";
        try {
            existStmt = conn.prepareStatement(query);
            existStmt.setString(1, token);
            existStmt.setInt(2, docID);
            ResultSet rs = existStmt.executeQuery();
            if (rs.next()) {
                String offsets = rs.getString(3);
                return offsets;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     *
     */
    public PostingsList getPostingList(String token) {
        String query = "SELECT docID, offset FROM DavisIndex WHERE token = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            //Now create the postingslist and stuff.
            PostingsList list = new PostingsList();
            rs.next();
            System.out.print(rs.next());
            int currentDoc = rs.getInt(1);
            int currentOffset = rs.getInt(2);
            PostingsEntry entry = new PostingsEntry(currentDoc, currentOffset);
            list.insert(entry, currentOffset);
            while(rs.next()) {
                //TODO: Build a postings entry with docID and the offsets.
                currentDoc = rs.getInt(1);
                currentOffset = rs.getInt(2);
                entry = new PostingsEntry(currentDoc, currentOffset);
                list.insert(entry, currentOffset);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void newinsert(String token, int docID, int offset) {
        String insertQuery = "INSERT INTO DavisIndex (token, docID, offset)" +
                "VALUES(?,?,?)";
        String updateQuery = "UPDATE DavisIndex SET offset = ? WHERE token = ? AND docID = ?";
        //NOTE: STRING OF OFFSETS! "1 , 3 , 6, 19, 39" and so on......
        try {
            String offsets = existsToken(token, docID);
            pStmt = conn.prepareStatement(updateQuery);
            if (offsets != null) {
                counter++;
                //Update entry by adding new offset to offset string
                offsets = offsets + " " + offset; //Append latest offset to list. Mabye use SB for speed.
                pStmt.setString(1, offsets);
                pStmt.setString(2, token);
                pStmt.setInt(3, docID);
                pStmt.executeUpdate();
                //Don't think we need to clear?
                //pStmt.clearParameters();


            } else {
                //Create new entry
                counter++;
                pStmt = conn.prepareStatement(insertQuery);
                pStmt.setString(1, token);
                pStmt.setInt(2, docID);
                pStmt.setString(3, String.valueOf(offset));
                pStmt.executeUpdate();
                //Don't think we need to clear?
                //pStmt.clearParameters();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /**
         * Counter action below
         */

        if (counter % 10000 == 0) {
            System.out.println("Current records: " + counter);
            try {
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void insert(String token, int docID, int offset) {
        String sqlInsert = "INSERT INTO DavisIndex (token, docID, offset)" +
                "VALUES(?,?,?)";
        try {
            pStmt = conn.prepareStatement(sqlInsert);
            pStmt.getConnection().setAutoCommit(false);
           // if(counter != chunk) {
                counter++;
                pStmt.setString(1, token);
                pStmt.setInt(2, docID);
                pStmt.setInt(3, offset);
                pStmt.executeUpdate();
                pStmt.clearParameters();
                pStmt.close();
            if (counter % 100000 == 0) {
                System.out.println("Current records: " + counter);
                conn.commit();
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

    /**
     *
     */
    public void commitTransaction() {
        try {
            System.out.println("Transaction committed.");
            System.out.println("Total records: " + counter);
            conn.commit();
            conn.setAutoCommit(true);
            pStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

