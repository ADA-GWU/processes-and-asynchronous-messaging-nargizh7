## Installation
Follow these steps to install and run the application on your machine:
1. Clone the application repository to your local machine: git clone https://github.com/ADA-GWU/processes-and-asynchronous-messaging-nargizh7.git
2. Change your working directory to the project directory: cd processes-and-asynchronous-messaging-nargizh7
3. Make sure you have the PostgreSQL JDBC driver (JAR file) on your system. You can download it from the official PostgreSQL website or use the one provided in this project. You can use the following link: https://jdbc.postgresql.org/download/
4. Enter the "src" directory where the code files are located: cd src
5. Compile the Java files with the PostgreSQL JDBC driver included in the classpath in two terminal windows (one for the Sender, one for the Reader):
   javac -cp "path/to/postgresql-42.6.0.jar" Sender.java Reader.java
Example: PS C:\Users\new-eclipse-workspace\AsynchMessaging\src> javac -cp "C:\Users\nargi\Desktop\postgresql-42.6.0.jar" Sender.java Reader.java
   
## Running the Sender and Reader
1. The Sender program allows you to insert messages into the database.
In one terminal window, execute the Sender: java -cp ".;path/to/postgresql-42.6.0.jar" Sender
Example: PS C:\Users\new-eclipse-workspace\AsynchMessaging\src> java -cp ".;C:\Users\nargi\Desktop\postgresql-42.6.0.jar" Sender
2. The Reader program retrieves and displays messages from the database.
In another terminal window, execute the Reader: java -cp ".;path/to/postgresql-42.6.0.jar" Reader
Example: PS C:\Users\new-eclipse-workspace\AsynchMessaging\src> java -cp ".;C:\Users\nargi\Desktop\postgresql-42.6.0.jar" Reader

## Usage
The Sender program allows you to insert messages into the ASYNC_MESSAGES table in the database. You will be prompted to enter a message, which will be associated with your name and the current time.
The Reader program retrieves and displays messages from the ASYNC_MESSAGES table that meet the following criteria:
The RECEIVED_TIME column is NULL.
The SENDER_NAME is not your name.
The Reader will display the available messages and mark them as received with the current time to avoid multiple readers accessing the same message simultaneously.
To change the list of IPs of DBs to which you want to connect, just change their list at the end of both the Sender.java and Reader.java files. Save the changes, and run the application again.


   




