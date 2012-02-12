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

    // current private msg client has seen
    // using queue instead
    //private int currentPrivMsg;

    // Socket to client
    private Socket client;

    // Input from client
    //private BufferedReader input;

    // inputStream from socket is read into this byte array
    byte[] b;

    // exiting flag
    boolean exiting;
    // Has client logged in?
    private boolean loggedIn;

    // name of client
    private String name;

    private InputStream inputStream;
    // output writer to client
    private PrintWriter output;
    
    // client id
    private int cid;
    
    // reference to MessageDaemon
    private MessageDaemon md;

    // grab input and output streams from Socket
    public ChatConnection(Socket s, int id, MessageDaemon m) {
        privMessages = new LinkedList<String>();
        currentMsg = 0;
        exiting = false;
        
        client = s;
        md = m;
        try {
            inputStream = client.getInputStream();
            //     instream = new ByteArrayInputStream(client.getInputStream());
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
            check_priv_messages();
            
            // check send all messages
            check_all_messages();
            
            // check if exiting has been set
            if(exiting == true) {
                try {
                    client.close();
                    inputStream.close(); 
                } catch (Exception e) {
                }
            }
        } while (true && exiting == false);
    }
   

    public void processMsg(String inStr) {
        // First char should be msg type.
        //msgType = inStr.substring(0,1);
        // chop off message type
        //StringTokenizer st = new StringTokenizer(inStr);
        //String cmd = st.nextToken();
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
                md.privateMsg(inStr, name, receiver);
            } else {
                // if there is just one word, send as a send all message
                sendMsg(inStr);
            }
        } else if (cmd.equals("LOGIN")) {
            login(inStr);
        } else if (cmd.equals("LOGOUT")) {
            logout();
        } else {
            System.out.println("don't recognize msgType: " + cmd + ", with string: " + inStr);
        }
    }

    // client has asked to log out, 
    // disconnect connection and send a message to all.
    private void logout() {
        try {
            if(client != null) {
                System.out.println("Closing down connection with " + cid);
            }

            // start message to all with SENDALL msgtype
            md.addMsg(name + " has left the chat room.");

            // remove this chatClient from MessageDaemon
            md.removeChat(this);
        } catch (IOException ie) {
            System.out.println("Unable to disconnect");
        } 
        exiting = true;
    }

    // Send Message to all users
    public void sendMsg(String msg) {
        // have Message Daemon add msg to queue
        md.addMsg(name + ": " + msg);
    }

    // send a private Message to a user
    // public void sendPrivate(String msg, String sender, String rec) {
    //     md.sendPrivate(msg, sender, rec);
    // }
    
    // Add message to the Chat Connection's private message Queue.
    public void addPrivMsg(String inStr) {
        privMessages.add(inStr);
    }

    // Return Chat client's name.
    public String getClientName() {
        return name;
    }
   
    public void check_priv_messages() {
        String msg;
        if((msg = privMessages.poll())!= null) {
            output.println(msg);
        }
    }

    public void check_all_messages() {
        // check if there is a new message
        if(md.getMessagesSize() > currentMsg ) {
            currentMsg++;
            System.out.println("checking message #" + currentMsg);
            output.println(md.getMessage(currentMsg));
        }
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
        if(checkPassword(inName,inPassword)) {
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

/**
private class ChatConnectionWriter extends Thread {
    private PrintWriter output;

    // reference to MessageDaemon
    private MessageDaemon md;

    // Queue of private messages
    //private Queue<String> privMessages;
 

    public ChatConnectionWriter(PrintWriter outp, MessageDaemon msgD) {
        privMessages = new LinkedList<String>();
        md = msgD;
        output = outp;
    }

    public void check_priv_messages() {
        String msg;
        if((msg = privMessages.poll())!= null) {
            output.println(msg);
        }
    }

    public void check_all_messages() {
        // check if there is a new message
        if(md.getMessagesSize() > currentMsg ) {
            currentMsg++;
            System.out.println("checking message #" + currentMsg);
            output.println(md.getMessage(currentMsg));
        }
    }

    
    public void run() {
        do {
            // check for private message
            check_priv_messages();
            
            // check send all messages
            check_all_messages();
           
        } while(true)
    }
}
*/
