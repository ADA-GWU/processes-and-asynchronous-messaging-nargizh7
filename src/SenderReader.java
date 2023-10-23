import java.sql.*;
import java.io.*;
import java.security.Timestamp;
import java.util.Scanner;

public class SenderReader {
    public static void main(String[] args) {
        String[] databaseServers = {"dbServer1", "dbServer2", "dbServer3"}; // List of database servers
        int numberOfThreads = databaseServers.length; // Number of threads to create
        Thread[] threads = new Thread[numberOfThreads]; // Array of threads

        // Create threads for each database server
        for (int i = 0; i < numberOfThreads; i++) {
            final int serverIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    // Connect to the database server
                    Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://" + databaseServers[serverIndex] + ":3306/mydatabase", "username", "password");

                    // Create a statement object
                    Statement statement = databaseConnection.createStatement();

                    // Request user input
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter a message: ");
                    String userMessage = scanner.nextLine();

                    // Insert a record into ASYNC_MESSAGE table with sender name, entered message, and current time
                    String senderName = "Nargiz"; // Replace with your name
                    String currentTime = new Timestamp(System.currentTimeMillis()).toString();
                    String sql = "INSERT INTO ASYNC_MESSAGE (SENDER_NAME, MESSAGE, CURRENT_TIME) VALUES ('" + senderName + "', '" + userMessage + "', '" + currentTime + "')";
                    statement.executeUpdate(sql);

                    // Close the statement and connection objects
                    statement.close();
                    databaseConnection.close();
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

        // Check available messages in each database server test test test
        for (String server : databaseServers) {
            try {
                // Connect to the database server
                Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://" + server + ":3306/mydatabase", "username", "password");

                // Create a statement object
                Statement statement = databaseConnection.createStatement();

                // Select an available message from ASYNC_MESSAGE table and update its RECEIVED_TIME field with current time
                String senderName = "Nargiz"; // Replace with your name
                String currentTime = new Timestamp(System.currentTimeMillis()).toString();
                String sql = "SELECT MESSAGE, CURRENT_TIME FROM ASYNC_MESSAGE WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != '" + senderName + "' FOR UPDATE";
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    String message = resultSet.getString("MESSAGE");
                    String receivedTime = resultSet.getString("CURRENT_TIME");
                    System.out.println("Sender " + resultSet.getString("SENDER_NAME") + " sent \"" + message + "\" at time " + receivedTime);
                    sql = "UPDATE ASYNC_MESSAGE SET RECEIVED_TIME='" + currentTime + "' WHERE MESSAGE='" + message + "'";
                    statement.executeUpdate(sql);
                }

                // Close the result set, statement, and connection objects
                resultSet.close();
                statement.close();
                databaseConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
