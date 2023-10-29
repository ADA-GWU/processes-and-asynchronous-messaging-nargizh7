import java.sql.*;
import java.util.*;

public class Sender implements Runnable {

    private String dbIP; // the IP address of the database server
    private String dbName = "hw1"; // the name of the database
    private String dbUser = "dist_user"; // the username of the database
    private String dbPass = "dist_pass_123"; // the password of the database
    private Connection conn; // the connection object to the database

    public Sender(String dbIP) {
        this.dbIP = dbIP;
    }

    public void run() {
        try {
            // connect to the database server
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbIP + "/" + dbName, dbUser, dbPass);
            System.out.println("Connected to database " + dbIP);

            // create a scanner object to read user input
            Scanner sc = new Scanner(System.in);

            // loop until interrupted
            while (true) {
                // prompt the user to enter a text message
                System.out.print("Enter a text message: ");
                String message = sc.nextLine();

                // insert a record into the ASYNC_MESSAGES table with the sender name, message and current time
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ASYNC_MESSAGES (SENDER_NAME, MESSAGE, SENT_TIME) VALUES (?, ?, ?)");
                pstmt.setString(1, "NargizH");
                pstmt.setString(2, message);
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

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
