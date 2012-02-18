import java.sql.*;
import java.util.*;

public class DBConnection {
    Connection conn = null;
    String userName;
    String password;
    String url;
    HashMap<String,String> logins;

    public DBConnection (String user, String pass) {
        try {
            //String userName = "chatter";
            //String password = "xXxXxXxXxXx";
            userName = user;
            password = pass;
            logins = new HashMap<String,String>();
            url = "jdbc:mysql://173.230.154.132/chatserver";
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");
        } catch (Exception e) {
            System.err.println ("Cannot connect to database server");
        }
        
        // Set up the logins
        setUpLogins();

        try {
            conn.close();
            System.out.println("Database connection terminated.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // finally
        //     {
        //         if (conn != null)
        //             {
        //                 try
        //                     {
        //                         conn.close ();
        //                         System.out.println ("Database connection terminated");
        //                     }
        //                 catch (Exception e) { /* ignore close errors */ }
        //             }
        //     }
    }
    
    /**
     * Populates the logins hashmap with reistered users
     *
     */
    public void setUpLogins() {
        String nameVal = "";
        String passVal = "";
        try {
            Statement s = conn.createStatement();
            String query = "Select name,password from account";
            s.executeQuery(query);
            ResultSet rs = s.getResultSet();
            while(rs.next() ) {
               nameVal = rs.getString ("name");
               passVal = rs.getString ("password");
               logins.put(nameVal,passVal);
            }
            rs.close();
            s.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }


    public boolean checkPassword (String username, String password) {
        int idVal;
        String nameVal = "";
        String passVal = "";
        
        try {
            
            Statement s = conn.createStatement ();
            //String query = "Select id,name,password from account where name='" + username + "' and password=SHA1'" + password + "')";
            String query = "Select id,name,password from account where name='" + username + "' and password='" + password + "'";
            s.executeQuery(query);
            ResultSet rs = s.getResultSet ();
            while (rs.next ()) {
                idVal = rs.getInt ("id");
                nameVal = rs.getString ("name");
                passVal = rs.getString ("password");
                if(username.equals(nameVal) ) {
                    System.out.println("account exists");
                    return true;
                }
            }
            rs.close ();
            s.close ();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }
    public boolean registerUser(String username, String password) {
        // check to see if username is already being used.
        if(logins.containsKey(username)) {
                return false;
        }

        // if connection has died, re-establish it.
        if(conn == null) {
            try {
                conn = DriverManager.getConnection (url, userName, password);
                System.out.println ("Database connection re-established");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        try {
           Statement s = conn.createStatement ();
           String query = "Select id,name,password from account where name='" + username + "'";
           s.executeQuery(query);
           ResultSet rs = s.getResultSet ();
           while (rs.next ()) {
               // Return false if username is already in account db. 
               return false;
           }
           rs.close ();
           s.close ();

           // Since user doesn't already exist, add user to logins
           logins.put(username,password);
           // Got this far account with that name doesn't exist

           // insert new account with username, password
           s = conn.createStatement ();
           //query = "insert into account (name,password) values('" + username + "',SHA1('" + password + "'))";
           query = "insert into account (name,password) values('" + username + "','" + password + "')";
           System.out.println(query);
           s.executeUpdate(query);
           // ResultSet rs = s.getResultSet ();
           // while (rs.next ()) {
           //     return false;
           // }
           // rs.close ();
           s.close ();
           return true;
           
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return true;

    }
    
}