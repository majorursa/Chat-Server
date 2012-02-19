import java.sql.*;
import java.util.*;

public class DBConnection {
    Connection conn = null;
    String dbUsername;
    String dbPassword;
    String url;
    HashMap<String,String> logins;

    public DBConnection (String user, String pass) {
        try {
            //String dbUsername = "chatter";
            //String dbPassword = "xXxXxXxXxXx";
            dbUsername = user;
            dbPassword = pass;
            logins = new HashMap<String,String>();
            url = "jdbc:mysql://173.230.154.132/chatserver";
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection (url, dbUsername, dbPassword);
            System.out.println ("Database connection established");
        } catch (Exception e) {
            System.err.println ("Cannot connect to database server");
        }
        
        // Set up the logins
        setUpLogins();

        // close the database connection
        try {
            conn.close();
            System.out.println("Database connection terminated.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn = null;

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
        passVal=logins.get(username);
        if(passVal != null && passVal.equals(password)) {
            return true;
        }
        return false;
                
    }
    public boolean registerUser(String username, String password) {
        // check to see if username is already being used.
        if(logins.containsKey(username)) {
                return false;
        }

        try {
            //System.out.println("url: "+url + " username: " + dbUsername + " pass: " + dbPassword);
            //Class.forName ("com.mysql.jdbc.Driver").newInstance ();

            // Re-establish Database Connection
            conn = DriverManager.getConnection (url, dbUsername, dbPassword);
            System.out.println ("Database connection re-established");

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
            //System.out.println(query);
            s.executeUpdate(query);
            s.close ();
            conn.close();
            conn = null;
            return true;
           
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } 
        return true;

    }
    
}