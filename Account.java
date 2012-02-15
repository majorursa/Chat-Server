import java.util.*;

public class Account {
    private String username;
    private String password;
    private boolean loggedIn;

    public Account(String name, String pass) {
        username = name;
        password = pass;
        loggedIn = false;
    }
    
    // get logged in status
    public boolean getLoggedin() {
        return loggedIn;
    }

    // set logged in status
    public void setLoggedIn(boolean p) {
        loggedIn = p;
    }

    public void changePassword(String pass) {
        password = pass;
    }
}