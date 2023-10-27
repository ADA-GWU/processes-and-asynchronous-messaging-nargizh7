# processes-and-asynchronous-messaging-nargizh7
processes-and-asynchronous-messaging-nargizh7 created by GitHub Classroom

This is a Java program that implements asynchronous concurrent messaging using PostgreSQL databases. 
The program consists of two parts: a sender and a reader.

## Installation:
Clone the application repository to your local machine: git clone https://github.com/ADA-GWU/processes-and-asynchronous-messaging-nargizh7.git
Change your working directory to the project directory: cd processes-and-asynchronous-messaging-nargizh7
Enter the "src" directory where the code files are located: cd src

## Sender
The sender  reads the list of database server IPs and connects to all the databases in different threads. 
The user can enter a text message and the program will choose one of the threads and insert a record into ASYNC_MESSAGES table with the sender name, the message and the current time.

## Reader
The reader checks available messages in each database. 
An available message is one that has not been received by any reader and has a different sender name than the reader. 
The reader will pick one message like this, show it on the terminal as Sender XXX sent XXX at time XXXX, and then set the received time to the current time. 
The reader will also block the record while reading to prevent other readers from accessing the same message.

## How to run
To run the program, you need to have Java installed on your machine and PostgreSQL drivers in your classpath. 
You also need to have access to PostgreSQL databases with the following configuration:

- Access from any other IPs with dist_user/dist_pass_123 credentials.
- When an image is created, create the following table:
ASYNC_MESSAGES (
 RECORD_ID auto increment number,
 SENDER_NAME varchar(30),
 MESSAGE varchar(30),
 SENT_TIME DATE/TIME,
 RECEIVED_TIME DATA/TIME)

To compile the program, use the following command:
`javac Sender.java Reader.java`
To run the sender software, use the following command:
`java Sender`
To run the reader software, use the following command:
`java Reader`
To exit the program, type "exit" in the terminal.

