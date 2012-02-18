import java.io.*;
import java.net.*;
import java.util.*;

/**
 * MessageDaemon
 * 
 * Holds the messages object which holds all of the public messages
 * Holds the chats object which holds all of the ChatConnections objects
 *
 */
public class MessageDaemon extends Thread {
    public ArrayList<String> messages;
    public ArrayList<ChatConnection> chats;
    private int MAXCLIENTS;

    public MessageDaemon(int max) {
        MAXCLIENTS = max;
        messages = new ArrayList<String>(100);
        chats = new ArrayList<ChatConnection>(MAXCLIENTS);
    }
    
    public String who() {
        String wholist = "";
        for (ChatConnection cc : chats) {
            wholist += cc.getClientName() + " is logged in. ";
        } 
        // chop off last ", "
        //wholist = wholist.substring(0,wholist.length()-2);
        return wholist;
    }
    public void addChat(ChatConnection cc) {
        chats.add(cc);
    }

    public void removeChat(ChatConnection cc) {
        // remove the ChatConnection, they have called logout
        chats.remove(cc);
    }

    // return size of messages queue
    public synchronized int getMessagesSize() {
        return messages.size();
    }

    // return message from messages
    public synchronized String getMessage(int mid) {
        return messages.get(mid-1);
    }

    // Check if user already logged in
    public boolean checkLoggedIn(String name) {
        String cname = "";
        for(ChatConnection cc : chats) {
            cname = cc.getClientName();
            if(name.equals(cname)) {
                return true;
            } 
        }
        return false;
    }

    // Add a message from <from> into <to>'s private message queue.
    public void privateMsg (String msg, String from, String to) {
        String cname = "";
        for(ChatConnection cc : chats) {
            cname = cc.getClientName();
            if(to.equals(cname)) {
                cc.addPrivMsg(from + " (to " + to + "): " + msg);
            } 
        }
    }
    
    public void addMsg(String msg) {
        //ChatConnection cc = Chats.get(cid);

        // Get name of message sender.
        //String name = cc.getClientName();

        // Synchronize on the message queue.
        synchronized(messages) {
            // add message to the Messages Queue
            messages.add(msg);
        }
    }
    
}
