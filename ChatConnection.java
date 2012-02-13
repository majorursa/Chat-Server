import java.io.*;
import java.net.*;
import java.util.*;

public class ChatConnection extends Thread {
    // type of message from client
    public enum MsgType {
        SEND, SENDALL, WHO, LOGIN, LOGOUT;
    }

    // Queue of messages
    private Queue<String> privMessages;

    // current msg client is on
    private int currentMsg;

    // Socket to client
    private Socket client;

    // inputStream from socket is read into this byte array
    byte[] b;

    // exiting flag
    boolean exiting;
    // Has client logged in?
    private boolean loggedIn;

    // name of client
    private String name;

    // Use InputStream for non-blocking IO call available
    private InputStream inputStream;

    // output writer to client
    private PrintWriter output;
    
    // client id
    private int cid;
    
    // reference to MessageDaemon
    private MessageDaemon md;
    
    // reference to database connection
    private DBConnection db;

    // grab input and output streams from Socket
    public ChatConnection(Socket s, int id, MessageDaemon m, DBConnection dbc) {
        privMessages = new LinkedList<String>();
        md = m;
        // set the current message for this client at the current size of the messages 
        currentMsg = md.getMessagesSize();
        exiting = false;
        db = dbc;
        client = s;
        
        try {
            inputStream = client.getInputStream();
            output = new PrintWriter(client.getOutputStream(), true);
            cid = id;
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void run() {
        String received = null;
        int bytes;
        int maxbytes=1024;
        
        do {
            received = "";
            try {
                if(inputStream.available() > 2) {
                    byte[] buf = new byte[ inputStream.available() ];
                    inputStream.read( buf );
                    received = new String(buf);
                    received = received.trim();
                } 
            } catch (IOException ie) {
                //inputStream.close();
                ie.printStackTrace();
            }

            if(received.length() > 1) {
                // DEBUG echo received msg
                System.out.println("received from " + cid + ": " + received);
                // process the msg
                processMsg(received);
            }

            // check for private message
            checkPrivateMessages();

            if (loggedIn == true) {
                // check send all messages
                checkAllMessages();
            }
            
            // check if exiting has been set
            if(exiting == true) {
                try {
                    client.close();
                    inputStream.close(); 
                } catch (Exception e) {
                }
            }
            
            // sleep interval, do I need this??
            try {
                sleep(50);
            } catch (InterruptedException ine) {
            }
        } while (exiting == false);
    }
   

    public void processMsg(String inStr) {
        int space;
        String cmd;
        String receiver = "";
        
        // is this a 1 word command?
        space = inStr.indexOf(' ');
        if(space > 0) {
            cmd = inStr.substring(0,space);
            inStr = inStr.substring(space+1);
        } else { 
            cmd = inStr;
        }

        cmd = cmd.toUpperCase();

        // Send a message to login, if not logged in
        if (loggedIn == false && !cmd.equals("LOGIN") && !cmd.equals("REGISTER")) {
            privMessages.add("Please Login first.");
            System.out.println("command is: " + cmd);
        } else {
            //System.out.println("logged in status: " + loggedIn);
            if (cmd.equals("WHO")) {
                callWho();
            } else if (cmd.equals("SENDALL")) {
                sendMsg(inStr);
            } else if (cmd.equals("SEND")) {
                // break off name of receiver
                space = inStr.indexOf(' ');
                if(space > 0) {
                    receiver = inStr.substring(0,space);
                    inStr = inStr.substring(space+1);
                    if(receiver.equals("all")) {
                        sendMsg(inStr);
                    } else {
                        md.privateMsg(inStr, name, receiver);
                    }
                } else {
                    // if there is just one word, send as a send all message
                    sendMsg(inStr);
                }
            } else if (cmd.equals("LOGIN")) {
                login(inStr);
            } else if (cmd.equals("LOGOUT")) {
                logout();
            } else if (cmd.equals("REGISTER")) {
                space = inStr.indexOf(' ');
                if(space > 0) {
                    String u = inStr.substring(0,space);
                    String p = inStr.substring(space+1);
                    register(u,p);
                } else {
                    privMessages.add("Usage: register username password");
                }
            } else {
                // Send a message to user, telling them command is unrecognized.
                privMessages.add("Could not recognize command: " + cmd);
                // LOG message
                System.out.println("don't recognize msgType: " + cmd + ", with string: " + inStr);
            }
        }
    }

    // client has asked to log out, 
    // disconnect connection and send a message to all.
    private void logout() {
        if(client != null) {
            System.out.println("Closing down connection with " + cid);
        }

        // start message to all with SENDALL msgtype
        md.addMsg(name + " has left the chat room.");

        // remove this chatClient from MessageDaemon
        md.removeChat(this);
        exiting = true;
    }

    // Send Message to all users
    public void sendMsg(String msg) {
        // have Message Daemon add msg to queue
        md.addMsg(name + ": " + msg);
    }

    // Add message to the Chat Connection's private message Queue.
    public void addPrivMsg(String inStr) {
        privMessages.add(inStr);
    }

    // Return Chat client's name.
    public String getClientName() {
        return name;
    }
   
    public void checkPrivateMessages() {
        String msg;
        // while queue has more private message, write them to socket
        while((msg = privMessages.poll())!= null) {
            output.println(msg);
        }
    }

    public void checkAllMessages() {
        // check if there is a new message
        if(md.getMessagesSize() > currentMsg ) {
            currentMsg++;
            System.out.println("checking message #" + currentMsg);
            output.println(md.getMessage(currentMsg));
        }
    }

    public void register(String username, String password) {
        db.registerUser(username,password);
    }
    // who
    public void callWho() {
        String whoList = md.who();
        output.println(whoList);
    }


    public void login(String inStr) {
        // message: login username password
        StringTokenizer st = new StringTokenizer(inStr);
        // drop command, actually that's already chopped off
        // st.nextToken();
        String inName = st.nextToken();
        String inPassword = st.nextToken();
        if(db.checkPassword(inName,inPassword)) {
            name = inName;
            System.out.println(name + " has successfully logged in.");
            md.addMsg(name + " has logged in.");
            loggedIn = true;
        } else {
            System.out.println(inName + " failed to log in with password: " + inPassword);
        }
    }

    // check if password is valid
    public boolean checkPassword(String name, String pass) {
        return true;
    }
}

