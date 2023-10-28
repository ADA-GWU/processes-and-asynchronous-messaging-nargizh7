import java.io.*; 
import java.sql.*; 
import java.util.*; 
import java.util.concurrent.*;

public class Reader {
	// A list of database server IPs
	private static List<String> dbServers;

	// A thread pool to execute tasks
	private static ExecutorService executor;

	// The reader name
	private static final String READER_NAME = "NargizH";

	public static void main(String[] args) {
	    // Initialize the list of database servers from a file
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

	    // Initialize the thread pool with the same size as the number of database servers
	    executor = Executors.newFixedThreadPool(dbServers.size());

	    // Loop until the user presses Ctrl+C
	    while (true) {
	        // Check for available messages in each database server in parallel
	        for (String dbServer : dbServers) {
	            executor.execute(new CheckTask(dbServer));
	        }
	        // Wait for 1 second before checking again
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	            break;
	        }
	    }

	    // Shutdown the thread pool
	    executor.shutdown();
	}

	// A task that checks for available messages in a database server
	static class CheckTask implements Runnable {

	    // The database server IP
	    private String dbServer;

	    public CheckTask(String dbServer) {
	        this.dbServer = dbServer;
	    }

	    @Override
	    public void run() {
	        // Create a connection to the database server
	        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbServer + ":5432/postgres", "dist_user", "dist_pass_123")) {
	            // Create a statement to select an available message from the ASYNC_MESSAGES table
	            try (PreparedStatement stmt = conn.prepareStatement("SELECT RECORD_ID, SENDER_NAME, MESSAGE, SENT_TIME FROM ASYNC_MESSAGES WHERE RECEIVED_TIME IS NULL AND SENDER_NAME != ? LIMIT 1")) {
	                stmt.setString(1, READER_NAME);
	                ResultSet rs = stmt.executeQuery();
	                // If there is an available message, show it on the terminal and update the received time
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