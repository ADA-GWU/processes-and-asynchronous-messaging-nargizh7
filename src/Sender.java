import java.sql.*;
import java.util.*;

public class Sender implements Runnable {
    private List<String> dbIPs; // List of Database server IPs
    private String senderName = "NargizH"; // Your sender name
    private Random random = new Random();

    public Sender(List<String> dbIPs) {
        this.dbIPs = dbIPs;
    }

    public void run() {
        try {
            for (String dbIP : dbIPs) {
                // Connect to the database server using dbIP
                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbIP + "/hw1", "dist_user", "dist_pass_123")) {
                    System.out.println("Connected to database " + dbIP);

                    Scanner sc = new Scanner(System.in);

                    while (true) {
                        System.out.print("Enter a text message: ");
                        String message = sc.nextLine();

                        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ASYNC_MESSAGES (SENDER_NAME, MESSAGE, SENT_TIME) VALUES (?, ?, ?)")) {
                            pstmt.setString(1, senderName);
                            pstmt.setString(2, message);
                            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                            pstmt.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        Thread.sleep(random.nextInt(4000) + 1000);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<String> dbIPs = Arrays.asList("34.75.144.18", "34.75.123.81"); // Replace with your DB IPs

        Sender sender = new Sender(dbIPs);
        Thread senderThread = new Thread(sender);
        senderThread.start();
    }
}
