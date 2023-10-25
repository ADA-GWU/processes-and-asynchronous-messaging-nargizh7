import java.io.*;
import java.sql.*;
import java.io.*;
import java.util.*;

public class Sender {
// A list of database server IPs
private static final String[] DB_IPS = {"192.168.0.1", "192.168.0.2", "192.168.0.3"};

// A list of database connections
private static final Connection[] DB_CONNS = new Connection[DB_IPS.length];

// A list of threads for each database connection
private static final Thread[] DB_THREADS = new Thread[DB_IPS.length];

// The sender name
private static final String SENDER_NAME = "Nargiz";

// The database user name and password
private static final String DB_USER = "dist_user";
private static final String DB_PASS = "dist_pass_123";

// The SQL statement to insert a message into ASYNC_MESSAGES table
private static final String SQL_INSERT = "INSERT INTO ASYNC_MESSAGES (SENDER_NAME, MESSAGE, SENT_TIME) VALUES (?, ?, CURRENT_TIMESTAMP)";

public static void main(String[] args) {
    // Initialize the database connections and threads
    for (int i = 0; i < DB_IPS.length; i++) {
        try {
            // Connect to the database server with the given IP address
            DB_CONNS[i] = DriverManager.getConnection("jdbc:postgresql://" + DB_IPS[i] + ":5432/postgres", DB_USER, DB_PASS);

            // Create a thread for the database connection
            DB_THREADS[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Get the current thread index
                    int index = Thread.currentThread().getName().charAt(0) - '0';

                    // Get the current database connection
                    Connection conn = DB_CONNS[index];

                    // Prepare a statement to insert a message into ASYNC_MESSAGES table
                    try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
                        // Set the sender name parameter
                        stmt.setString(1, SENDER_NAME);

                        // Loop until the thread is interrupted
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                // Wait for a message from the user input
                                String message = getMessage();

                                // Set the message parameter
                                stmt.setString(2, message);

                                // Execute the statement and insert the message into the table
                                stmt.executeUpdate();

                                // Print a confirmation message to the console
                                System.out.println("Message sent to database " + index + ": " + message);
                            } catch (InterruptedException e) {
                                // The thread is interrupted, break the loop
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        // Print the SQL exception to the console
                        e.printStackTrace();
                    }
                }
            });

            // Set the thread name as the index of the database connection
            DB_THREADS[i].setName(String.valueOf(i));

            // Start the thread
            DB_THREADS[i].start();
        } catch (SQLException e) {
            // Print the SQL exception to the console
            e.printStackTrace();
        }
    }

    // Wait for the user to exit the program by typing "exit"
    while (true) {
        try {
            String input = getMessage();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
        } catch (InterruptedException e) {
            break;
        }
    }

    // Interrupt all threads and close all database connections
    for (int i = 0; i < DB_IPS.length; i++) {
        try {
            DB_THREADS[i].interrupt();
            DB_CONNS[i].close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    System.out.println("Sender program exited.");
}

// A method to get a message from the user input
private static String getMessage() throws InterruptedException {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter a message: ");
    String message = scanner.nextLine();
    return message;
}
}