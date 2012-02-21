import java.sql.*;
import java.util.*;

public class DBConnection {
    Connection conn = null;
    String dbUsername;
    String dbPassword;
    String url;
    HashMap<String,String> logins;

    public DBConnection () {
        try {
            logins = new HashMap<String,String>();
            url = "jdbc:sqlite:chataccount.db";

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:chataccount.db");
            System.out.println ("Database connection established");
        } catch (Exception e) {
            System.err.println ("Cannot connect to database server");
        }
        
        // Set up the logins
        setUpLogins();



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
            ResultSet rs = s.executeQuery(query);

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
            Statement s = conn.createStatement ();
            String query = "Select id,name,password from account where name='" + username + "'";
            ResultSet rs = s.executeQuery(query);
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
            query = "insert into account values ($next_id, '" + username + "','" + password + "');";
            //System.out.println(query);
            s.executeUpdate(query);
            s.close ();
            //conn.close();
            //conn = null;
            return true;
           
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } 
        return true;

    }
    
}