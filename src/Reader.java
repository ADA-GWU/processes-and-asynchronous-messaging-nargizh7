import java.io.InterruptedIOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

public class Reader {
// A list of database server IPs
private static final String[] DB_IPS = {"192.168.0.1", "192.168.0.2", "192.168.0.3"};

// A list of database connections
private static final Connection[] DB_CONNS = new Connection[DB_IPS.length];

// A list of threads for each database connection
private static final Thread[] DB_THREADS = new Thread[DB_IPS.length];

// The reader name
private static final String READER_NAME = "Nargiz";

// The database user name and password
private static final String DB_USER = "dist_user";
private static final String DB_PASS = "dist_pass_123";

// The SQL statement to select an available message from ASYNC_MESSAGES table
private static final String SQL_SELECT = "SELECT RECORD_ID, SENDER_NAME, MESSAGE, SENT_TIME FROM ASYNC_MESSAGES WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != ? FOR UPDATE";

// The SQL statement to update the received time of a message in ASYNC_MESSAGES table
private static final String SQL_UPDATE = "UPDATE ASYNC_MESSAGES SET RECEIVED_TIME = CURRENT_TIMESTAMP WHERE RECORD_ID = ?";

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

                    // Prepare a statement to select an available message from ASYNC_MESSAGES table
                    try (PreparedStatement stmt1 = conn.prepareStatement(SQL_SELECT)) {
                        // Set the reader name parameter
                        stmt1.setString(1, READER_NAME);

                        // Prepare a statement to update the received time of a message in ASYNC_MESSAGES table
                        try (PreparedStatement stmt2 = conn.prepareStatement(SQL_UPDATE)) {

                            // Loop until the thread is interrupted
                            while (!Thread.currentThread().isInterrupted()) {
                                try {
                                    // Begin a transaction
                                    conn.setAutoCommit(false);

                                    // Execute the statement and get the result set
                                    try (ResultSet rs = stmt1.executeQuery()) {
                                        // Check if there is an available message in the result set
                                        if (rs.next()) {
                                            // Get the record id, sender name, message and sent time from the result set
                                            int recordId = rs.getInt("RECORD_ID");
                                            String senderName = rs.getString("SENDER_NAME");
                                            String message = rs.getString("MESSAGE");
                                            Timestamp sentTime = rs.getTimestamp("SENT_TIME");

                                            // Print the message to the console
                                            System.out.println("Sender " + senderName + " sent " + message + " at time " + sentTime);

                                            // Set the record id parameter for updating the received time
                                            stmt2.setInt(1, recordId);

                                            // Execute the statement and update the received time of the message in the table
                                            stmt2.executeUpdate();
                                        }
                                    }

                                    // Commit the transaction
                                    conn.commit();
                                } catch (SQLException e) {
                                    // Rollback the transaction in case of any SQL exception
                                    conn.rollback();

                                    // Print the SQL exception to the console
                                    e.printStackTrace();
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Set the thread name as the index of the database connection
            DB_THREADS[i].setName(String.valueOf(i));

            // Start the thread
            DB_THREADS[i].start();
        } catch (SQLException e) {
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
        } catch ( InterruptedException e) {
            break;
        }
    }

    // Interrupt all threads and close all database connections
    for (int i = 0; i < DB_IPS.length; i++) {
        try {
            DB_THREADS[i].interrupt();
            DB_CONNS[i].close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    System.out.println("Reader program exited.");
}

// A method to get a message from the user input
private static String getMessage() throws InterruptedIOException {
    final Scanner scanner = new Scanner(System.in);
    System.out.print("Enter exit to stop reading: ");
    String message = scanner.nextLine();
    return message;
}
}