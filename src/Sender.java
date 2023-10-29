import java.io.*; 
import java.sql.*; 
import java.util.*; 
import java.util.concurrent.*;

public class Sender {
	private static List<String> dbServers;
	private static ExecutorService executor;
	private static Scanner scanner;
	private static final String SENDER_NAME = "NargizH";

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
	    scanner = new Scanner(System.in);

	    while (true) {
	        System.out.print("Enter a message or exit: ");
	        String message = scanner.nextLine();
	        if (message.equalsIgnoreCase("exit")) {
	            break;
	        }
	        int index = new Random().nextInt(dbServers.size());
	        executor.execute(new InsertTask(dbServers.get(index), message));
	    }
	    executor.shutdown();
	    scanner.close();
	}

	static class InsertTask implements Runnable {
	    private String dbServer;

	    private String message;

	    public InsertTask(String dbServer, String message) {
	        this.dbServer = dbServer;
	        this.message = message;
	    }

	    @Override
	    public void run() {
	        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbServer + ":5432/postgres", "dist_user", "dist_pass_123")) {
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