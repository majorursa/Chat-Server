import java.sql.*;

public class DBConnection {
    Connection conn = null;
    String userName;
    String password;

    public DBConnection (String user, String pass) {
        try {
            //String userName = "chatter";
            //String password = "xXxXxXxXxXx";
            userName = user;
            password = pass;
            String url = "jdbc:mysql://173.230.154.132/chatserver";
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");
        } catch (Exception e) {
            System.err.println ("Cannot connect to database server");
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
    
    public boolean checkPassword (String username, String password) {
        try {
           Statement s = conn.createStatement ();
           String query = "Select id,name,password from account where name='" + username + "' and password=SHA1('" + password + "')";
           s.executeQuery(query);
           ResultSet rs = s.getResultSet ();
           while (rs.next ()) {
               int idVal = rs.getInt ("id");
               String nameVal = rs.getString ("name");
               String passVal = rs.getString ("password");
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
        try {
           Statement s = conn.createStatement ();
           String query = "Select id,name,password from account where name='" + username + "'";
           s.executeQuery(query);
           ResultSet rs = s.getResultSet ();
           while (rs.next ()) {
               return false;
           }
           rs.close ();
           s.close ();

           // Got this far account with that name doesn't exist

           // insert new account with username, password
           s = conn.createStatement ();
           query = "insert into account (name,password) values('" + username + "',SHA1('" + password + "'))";
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