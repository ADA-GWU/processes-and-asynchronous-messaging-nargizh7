import java.sql.*;
import java.util.*;

public class Reader implements Runnable {
    private List<String> dbIPs; // List of Database server IPs

    public Reader(List<String> dbIPs) {
        this.dbIPs = dbIPs;
    }

    public void run() {
        try {
            for (String dbIP : dbIPs) {
                // Connect to the database server using dbIP
                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbIP + "/hw1", "dist_user", "dist_pass_123")) {
                    System.out.println("Connected to database " + dbIP);

                    while (true) {
                        Statement stmt = conn.createStatement();

                        ResultSet rs = stmt.executeQuery("SELECT * FROM ASYNC_MESSAGES WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != 'NargizH' FOR UPDATE");

                        if (rs.next()) {
                            int record_id = rs.getInt("RECORD_ID");
                            String sender_name = rs.getString("SENDER_NAME");
                            String message = rs.getString("MESSAGE");
                            Timestamp sent_time = rs.getTimestamp("SENT_TIME");

                            System.out.println("Sender " + sender_name + " sent " + message + " at time " + sent_time);

                            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE ASYNC_MESSAGES SET RECEIVED_TIME = ? WHERE RECORD_ID = ?")) {
                                pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                                pstmt.setInt(2, record_id);
                                pstmt.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        rs.close();
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
        // Check if the user provided the list of DB IPs as command-line arguments
        if (args.length == 0) {
            System.out.println("Please specify the database server IPs as command-line arguments.");
            return;
        }

        List<String> dbIPs = Arrays.asList(args);

        Reader reader = new Reader(dbIPs);
        Thread readerThread = new Thread(reader);
        readerThread.start();
    }
}