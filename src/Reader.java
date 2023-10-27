import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

public class Reader {
    private static final String[] DB_IPS = {"192.168.0.1", "192.168.0.2", "192.168.0.3"};
    private static final String READER_NAME = "Nargiz";
    private static final String DB_USER = "dist_user";
    private static final String DB_PASS = "dist_pass_123";
    private static final String SQL_SELECT = "SELECT RECORD_ID, SENDER_NAME, MESSAGE, SENT_TIME FROM ASYNC_MESSAGES WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != ? FOR UPDATE";
    private static final String SQL_UPDATE = "UPDATE ASYNC_MESSAGES SET RECEIVED_TIME = CURRENT_TIMESTAMP WHERE RECORD_ID = ?";

    public static void main(String[] args) {
        for (String dbIp : DB_IPS) {
            Thread thread = new Thread(() -> {
                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbIp + ":5432/postgres", DB_USER, DB_PASS)) {
                    try (PreparedStatement stmt1 = conn.prepareStatement(SQL_SELECT);
                         PreparedStatement stmt2 = conn.prepareStatement(SQL_UPDATE)) {

                        stmt1.setString(1, READER_NAME);

                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                conn.setAutoCommit(false);

                                try (ResultSet rs = stmt1.executeQuery()) {
                                    if (rs.next()) {
                                        int recordId = rs.getInt("RECORD_ID");
                                        String senderName = rs.getString("SENDER_NAME");
                                        String message = rs.getString("MESSAGE");
                                        Timestamp sentTime = rs.getTimestamp("SENT_TIME");

                                        System.out.println("Sender " + senderName + " sent " + message + " at time " + sentTime);

                                        stmt2.setInt(1, recordId);
                                        stmt2.executeUpdate();
                                    }
                                }

                                conn.commit();
                            } catch (SQLException e) {
                                conn.rollback();
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter 'exit' to stop reading: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
        }

        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            thread.interrupt();
        }

        System.out.println("Reader program exited.");
    }
}
