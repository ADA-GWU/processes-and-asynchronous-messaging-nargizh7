## Installation
Follow these steps to install and run the application on your machine:
1. Clone the application repository to your local machine: <code> git clone https://github.com/ADA-GWU/processes-and-asynchronous-messaging-nargizh7.git </code>
2. Change your working directory to the project directory: <code> cd processes-and-asynchronous-messaging-nargizh7 </code>
4. Enter the "src" directory where the code files are located: <code> cd src </code>
5. Make sure you have the PostgreSQL JDBC driver (JAR file) on your system. You can download it from the official PostgreSQL website or use the one provided in this project.
   You can use the following link: <code> https://jdbc.postgresql.org/download/ </code>
6. Compile the Java files with the PostgreSQL JDBC driver included in the classpath in two terminal windows (one for the Sender java file, one for the Reader java file): <code> javac -cp "path/to/postgresql-42.6.0.jar" Sender.java Reader.java </code>
Example: <code> PS C:\Users\nargiz\new-eclipse-workspace\AsynchMessaging\src> javac -cp "C:\Users\nargiz\Desktop\postgresql-42.6.0.jar" Sender.java Reader.java </code>
   
## Running the Sender and Reader
### Sender
In one terminal window, execute the Sender and specify the database server IPs as command-line arguments:<code> java -cp ".;path/to/postgresql-42.6.0.jar" Sender IP1 IP2 </code>
Example: <code> PS C:\Users\nargiz\new-eclipse-workspace\AsynchMessaging\src> java -cp ".;C:\Users\nargiz\Desktop\postgresql-42.6.0.jar" Sender 34.75.144.18 34.75.123.81 </code>
### Reader
In another terminal window, execute the Reader and specify the database server IPs as command-line arguments: <code> java -cp ".;path/to/postgresql-42.6.0.jar" Reader IP1 IP2 </code>
Example: <code> PS C:\Users\nargiz\new-eclipse-workspace\AsynchMessaging\src> java -cp ".;C:\Users\nargiz\Desktop\postgresql-42.6.0.jar" Reader 34.75.144.18 34.75.123.81 </code>

## Usage
The Sender program allows you to insert messages into the ASYNC_MESSAGES table in the database. You will be prompted to enter a message, which will be associated with your name and the current time.
The Reader program retrieves and displays messages from the ASYNC_MESSAGES table that meet the following criteria:
The RECEIVED_TIME column is NULL.
The SENDER_NAME is not your name.
The Reader will display the available messages and mark them as received with the current time to avoid multiple readers accessing the same message simultaneously.

   




