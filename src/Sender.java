import java.io.*; 
import java.sql.*; 
import java.util.*; 
import java.util.concurrent.*;

public class Sender {
	// A list of database server IPs
	private static List<String> dbServers;

	// A thread pool to execute tasks
	private static ExecutorService executor;

	// A scanner to read user input
	private static Scanner scanner;

	// The sender name
	private static final String SENDER_NAME = "NargizH";

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

	    // Initialize the scanner to read user input
	    scanner = new Scanner(System.in);

	    // Loop until the user enters "exit"
	    while (true) {
	        System.out.print("Enter a message or exit: ");
	        String message = scanner.nextLine();
	        if (message.equalsIgnoreCase("exit")) {
	            break;
	        }
	        // Choose a random thread to insert the message into a random database server
	        int index = new Random().nextInt(dbServers.size());
	        executor.execute(new InsertTask(dbServers.get(index), message));
	    }

	    // Shutdown the thread pool and close the scanner
	    executor.shutdown();
	    scanner.close();
	}

	// A task that inserts a message into a database server
	static class InsertTask implements Runnable {

	    // The database server IP
	    private String dbServer;

	    // The message to insert
	    private String message;

	    public InsertTask(String dbServer, String message) {
	        this.dbServer = dbServer;
	        this.message = message;
	    }

	    @Override
	    public void run() {
	        // Create a connection to the database server
	        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbServer + ":5432/postgres", "dist_user", "dist_pass_123")) {
	            // Create a statement to insert the message into the ASYNC_MESSAGES table
	            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO ASYNC_MESSAGES (SENDER_NAME, MESSAGE, SENT_TIME) VALUES (?, ?, CURRENT_TIMESTAMP)")) {
	                stmt.setString(1, SENDER_NAME);
	                stmt.setString(2, message);
	                stmt.executeUpdate();
	                System.out.println("Inserted message '" + message + "' into " + dbServer);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

}