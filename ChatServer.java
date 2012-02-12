import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static ServerSocket serverSocket;
    private static final int PORT = 1337;
    private static int clients;
    private static final int MAXCLIENTS = 5;

    public static void main(String[] args) throws IOException {
        clients = 0;
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
            System.out.println("\nAccepted a new client, id: "+ clients + ", remote port:" + client.getPort() + ", local port: " + client.getLocalPort() + "\n");
            ChatConnection chatc = new ChatConnection(client, clients, md);
            chatc.start();
            md.addChat(chatc);
        } while (true);
    }
}



