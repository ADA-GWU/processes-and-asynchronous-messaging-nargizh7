import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

public class Sender {
    private static final String[] DB_IPS = {"192.168.0.1", "192.168.0.2", "192.168.0.3"};
    private static final String SENDER_NAME = "Nargiz";
    private static final String DB_USER = "dist_user";
    private static final String DB_PASS = "dist_pass_123";
    private static final String SQL_INSERT = "INSERT INTO ASYNC_MESSAGES (SENDER_NAME, MESSAGE, SENT_TIME) VALUES (?, ?, CURRENT_TIMESTAMP";

    public static void main(String[] args) {
        Thread[] senderThreads = new Thread[DB_IPS.length];
        Connection[] connections = new Connection[DB_IPS.length];

        for (int i = 0; i < DB_IPS.length; i++) {
            final int dbIndex = i;

            senderThreads[dbIndex] = new Thread(() -> {
                try {
                    connections[dbIndex] = DriverManager.getConnection("jdbc:postgresql://" + DB_IPS[dbIndex] + ":5432/postgres", DB_USER, DB_PASS);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }

                try (Connection conn = connections[dbIndex];
                     PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

                    stmt.setString(1, SENDER_NAME);

                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            String message = getMessage();

                            stmt.setString(2, message);
                            stmt.executeUpdate();

                            System.out.println("Message sent to database " + dbIndex + ": " + message);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            senderThreads[dbIndex].start();
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter 'exit' to stop sending: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
        }

        for (Thread thread : senderThreads) {
            thread.interrupt();
        }

        for (Connection connection : connections) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Sender program exited.");
    }

    private static String getMessage() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a message: ");
        String message = scanner.nextLine();
        return message;
    }
}

