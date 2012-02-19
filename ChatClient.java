import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient extends JPanel implements ActionListener {
    private final static int port = 1337;
    private final static String host = "localhost";

    protected static JTextField textField;
    public static JTextArea textArea;
    private final static String newline = "\n";
    private Socket csock;
    protected static Queue<String> outMsg;
    private static ChatClientConnection cc;
    
    public ChatClient() {
        super(new GridBagLayout());

        System.out.println("size of queue: " + outMsg.size());        
        // Add a label 
        JLabel label = new JLabel("Welcome to Ursa Major Chatroom, Cadet!");

        textArea = new JTextArea(10,40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        textField = new JTextField(40);
        textField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String text = textField.getText();
                    //textArea.append(text + newline);
                    textField.setText("");
                
                    synchronized(outMsg) {
                        // add text field string to pout going queue.
                        outMsg.add(text);
                    }
                }
            });
        
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        c.fill = GridBagConstraints.CENTER;
        c.ipady=20;
        c.ipadx=10;
        add(label, c);

        c.fill = GridBagConstraints.BOTH;
        c.ipady = 10;
        c.ipadx = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady=4;
        c.ipadx=2;
        add(textField, c);

    }
    
    public void actionPerformed(ActionEvent e) {
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Chat Room of the Geeks!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChatClient());

        // display the window
        frame.pack();
        frame.setVisible(true);
        // grab focus for the textField
        textField.requestFocus();
    }
    
    public static void main(String[] args) {
        outMsg = new LinkedList<String>();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

        cc = new ChatClientConnection(outMsg);
        cc.chatLoop();   
    }

    protected static class ChatClientConnection {
        // Network Connections
        private Socket clientSocket;
        private PrintWriter outToServer;
        private InputStream inputStream;
        private String received;
        private String outs;
        private Queue<String> outMsg;
    
        public ChatClientConnection(Queue<String> outQ) {
            try {
                //brin = new BufferedReader(new InputStreamReader(System.in));
                outMsg = outQ;
                clientSocket = new Socket("localhost", 1337);
                
                outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
                //outToServer.println("login bossy wwwwru");
                inputStream = clientSocket.getInputStream();
                System.out.println("Connected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void chatLoop() {
            String msg;
            do {
                //synchronized(outMsg) {
                msg = outMsg.poll();
                //}
          
                try {
                    if (msg != null) {
                        outToServer.println(msg);
                    }
                    while(inputStream.available() > 2) {
                        byte[] buf = new byte[ inputStream.available() ];
                        inputStream.read( buf );
                        received = new String(buf);
                        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    synchronized (outMsg) {
                                        textArea.append(received);
                                    }
                                }
                            });
                    } 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

    
}
