import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient extends JPanel implements ActionListener {
    protected JTextField textField;
    public static JTextArea textArea;
    private final static String newline = "\n";
    private Socket csock;
    protected static Queue<String> outMsg;
    private final static String host = "localhost";
    private final static int port = 1337;
    private static ChatClientConnection cc;

    public ChatClient() {
        super(new GridBagLayout());

        // outgoing messages
        //outMsg = new LinkedList<String>();
        //outMsg.add("Welcome to Ursa Major!");
        System.out.println("size of queue: " + outMsg.size());        
        // Add a label 
        JLabel label = new JLabel("Welcome to Ursa Major, Cadet!");

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
        
        c.fill = GridBagConstraints.HORIZONTAL;
        add(label, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
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
    }
    
    
    public static void main(String[] args) {
        outMsg = new LinkedList<String>();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

        //System.out.println("size of queue: " + outMsg.size());
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
                //sleep(10);
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
                                    textArea.append(received);
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


// class SocketWorker extends SwingWorker<Void,String> {
//     @Override
//     public void doInBackground()   {
//         private Socket clientSocket;
//         private PrintWriter outToServer;
//         private InputStream inputStream;
//         private String received;
//         private String outs;
//         //private Queue<String> outMsg = new LinkedList<;
        
//         try {
//             //brin = new BufferedReader(new InputStreamReader(System.in));

//             clientSocket = new Socket("localhost", 1337);
//             outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
//             inputStream = clientSocket.getInputStream();
//             System.out.println("Connected");
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     @Override
//     protected void process (List<String> chunks) {
//         for(String message : chunks) {
//             textArea.append(message);
//         }
//     }
// }