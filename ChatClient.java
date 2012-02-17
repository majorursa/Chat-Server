import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add a label 
        JLabel label = new JLabel("Welcome to Ursa Major, Cadet!");
        frame.getContentPane().add(label);
        
        // display the window
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}