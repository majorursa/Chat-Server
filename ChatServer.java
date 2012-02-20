import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;

public class ChatServer {
    private static ServerSocket serverSocket;
    private static final int PORT = 1337;
    private static int clients;
    private static final int MAXCLIENTS = 5;
    private static DBConnection db;

    public static void main(String[] args) throws IOException {
        clients = 0;
        String dbuser = "chatter";
        String dbpass = "xXxXxXxXxXx";

        //Use command line arguments for database connection.
        if(args.length > 1) {
          dbuser = args[0];
          dbpass = args[1];
        }
        
        // Connection with account Table in chatserver Database
        db = new DBConnection(dbuser,dbpass);
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ioe) {
            System.out.println("\nUnable to listen on port: " + PORT);
            System.exit(1);
        }

        // the MessageDaemon holds references to ChatConnections
        MessageDaemon md = new MessageDaemon(MAXCLIENTS);

        do {
            // Wait for clients.
            Socket client = serverSocket.accept();
            clients++;
            System.out.println("\nAccepted a new client, id: " + clients +
                             ", remote port:" + client.getPort() + ", local port: " 
                             + client.getLocalPort() + "\n");
            // Pass the client socket, clientid, 
            // also pass refs to MessageDaemon and DBConnection to contructor.
            ChatConnection chatc = new ChatConnection(client, clients, md, db);
            // Start Chat Connection thread
            chatc.start();
            // Add the Chat Connection to the MessageDaemon
            md.addChat(chatc);
        } while (true);
    }
}



