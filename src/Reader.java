import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class Reader {
    private static List<String> dbServers;
    private static ExecutorService executor;
    private static final String READER_NAME = "NargizH";

    public static void main(String[] args) {
        dbServers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("db_servers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                dbServers.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        executor = Executors.newFixedThreadPool(dbServers.size());

        while (true) {
            for (String dbServer : dbServers) {
                executor.execute(new CheckTask(dbServer));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        executor.shutdown();
    }

    static class CheckTask implements Runnable {
        private String dbServer;

        public CheckTask(String dbServer) {
            this.dbServer = dbServer;
        }

        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbServer + ":5432/postgres", "dist_user", "dist_pass_123")) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT RECORD_ID, SENDER_NAME, MESSAGE, SENT_TIME FROM ASYNC_MESSAGES WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != ? LIMIT 1 FOR UPDATE")) {
                    stmt.setString(1, READER_NAME);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        int recordId = rs.getInt("RECORD_ID");
                        String senderName = rs.getString("SENDER_NAME");
                        String message = rs.getString("MESSAGE");
                        Timestamp sentTime = rs.getTimestamp("SENT_TIME");
                        System.out.println("Sender " + senderName + " sent '" + message + "' at " + sentTime + " from " + dbServer);
                        try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE ASYNC_MESSAGES SET RECEIVED_TIME = CURRENT_TIMESTAMP WHERE RECORD_ID = ?")) {
                            updateStmt.setInt(1, recordId);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

