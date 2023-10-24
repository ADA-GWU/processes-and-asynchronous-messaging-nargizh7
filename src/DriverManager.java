import java.sql.*;
import java.util.Scanner;

public class DriveManager {
    public static void main(String[] args) {
        String[] dbServers = {"dbServer1", "dbServer2", "dbServer3"}; // List of database servers
        int numThreads = dbServers.length; // Number of threads to create
        Thread[] threads = new Thread[numThreads]; // Array of threads

        // Create threads for each database server
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    // Connect to the database server
                    Connection conn = DriverManager.getConnection("jdbc:mysql://" + dbServers[index] + ":3306/mydatabase", "username", "password");

                    // Create a statement object
                    Statement stmt = conn.createStatement();

                    // Request user input
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter a message: ");
                    String message = scanner.nextLine();

                    // Insert a record into ASYNC_MESSAGE table with sender name, entered message, and current time
                    String senderName = "John Doe"; // Replace with your name
                    String currentTime = new Timestamp(System.currentTimeMillis()).toString();
                    String sql = "INSERT INTO ASYNC_MESSAGE (SENDER_NAME, MESSAGE, CURRENT_TIME) VALUES ('" + senderName + "', '" + message + "', '" + currentTime + "')";
                    stmt.executeUpdate(sql);

                    // Close the statement and connection objects
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish executing
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Check available messages in each database server
        for (String dbServer : dbServers) {
            try {
                // Connect to the database server
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + dbServer + ":3306/mydatabase", "username", "password");

                // Create a statement object
                Statement stmt = conn.createStatement();

                // Select an available message from ASYNC_MESSAGE table and update its RECEIVED_TIME field with the current time
                String senderName = "John Doe"; // Replace with your name
                String currentTime = new Timestamp(System.currentTimeMillis()).toString();
                String sql = "SELECT MESSAGE, CURRENT_TIME FROM ASYNC_MESSAGE WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != '" + senderName + "' FOR UPDATE";
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    String message = rs.getString("MESSAGE");
                    String receivedTime = rs.getString("CURRENT_TIME");
                    System.out.println("Sender " + rs.getString("SENDER_NAME") + " sent \"" + message + "\" at time " + receivedTime);
                    sql = "UPDATE ASYNC_MESSAGE SET RECEIVED_TIME='" + currentTime + "' WHERE MESSAGE='" + message + "'";
                    stmt.executeUpdate(sql);
                }

                // Close the result set, statement, and connection objects
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
