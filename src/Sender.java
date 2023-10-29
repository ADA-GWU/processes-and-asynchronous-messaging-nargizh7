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
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Connect to the database server
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbIP + "/" + dbName, dbUser, dbPass);
            System.out.println("Connected to database " + dbIP);

            // Create a scanner object to read user input
            Scanner sc = new Scanner(System.in);

            // Loop until interrupted
            while (true) {
                // Prompt the user to enter a text message
                System.out.print("Enter a text message: ");
                String message = sc.nextLine();

                // Insert a record into the ASYNC_MESSAGES table with the sender name, message, and current time
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ASYNC_MESSAGES (SENDER_NAME, MESSAGE, SENT_TIME) VALUES (?, ?, ?)");
                pstmt.setString(1, "NargizH");
                pstmt.setString(2, message);
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

                // Sleep for a random interval between 1 and 5 seconds
                Thread.sleep((int) (Math.random() * 4000) + 1000);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found. Make sure it is in your classpath.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    // Close the connection
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // You can run the Sender class from here if needed
        Sender sender = new Sender("34.75.144.18");
        Thread senderThread = new Thread(sender);
        senderThread.start();
    }
}
