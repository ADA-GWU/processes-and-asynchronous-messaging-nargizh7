import java.sql.*;
import java.util.*;

public class Reader implements Runnable {

    private String dbIP; // the IP address of the database server
    private String dbName = "hw1"; // the name of the database
    private String dbUser = "dist_user"; // the username of the database
    private String dbPass = "dist_pass_123"; // the password of the database
    private Connection conn; // the connection object to the database

    public Reader(String dbIP) {
        this.dbIP = dbIP;
    }

    public void run() {
        try {
            // connect to the database server
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbIP + "/" + dbName, dbUser, dbPass);
            System.out.println("Connected to database " + dbIP);

            // create a statement object to execute queries
            Statement stmt = conn.createStatement();

            // loop until interrupted
            while (true) {
                // query for an available message in the ASYNC_MESSAGES table
                ResultSet rs = stmt.executeQuery("SELECT * FROM ASYNC_MESSAGES WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != 'NargizH' FOR UPDATE");

                // if there is a message, display it and update the received time
                if (rs.next()) {
                    int record_id = rs.getInt("RECORD_ID");
                    String sender_name = rs.getString("SENDER_NAME");
                    String message = rs.getString("MESSAGE");
                    Timestamp sent_time = rs.getTimestamp("SENT_TIME");

                    System.out.println("Sender " + sender_name + " sent " + message + " at time " + sent_time);

                    PreparedStatement pstmt = conn.prepareStatement("UPDATE ASYNC_MESSAGES SET RECEIVED_TIME = ? WHERE RECORD_ID = ?");
                    pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(2, record_id);
                    pstmt.executeUpdate();
                }

                // close the result set
                rs.close();

                // sleep for a random interval between 1 and 5 seconds
                Thread.sleep((int) (Math.random() * 4000) + 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // close the connection
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

