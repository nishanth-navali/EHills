/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import java.io.Serializable;

public class Login implements Serializable, Comparable {
    private static final long serialVersionUID = -89187293563569244L;
    private String username;
    private String password;

    // CONSTRUCTOR
    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Assorted getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Comparable implementation for easy checking if the Login is valid
    @Override
    public int compareTo(Object o) {
        if (o instanceof Login) {
            if (this.username.equals(((Login) o).getUsername()) && this.password.equals(((Login) o).getPassword()))
                return 0;
            else return -1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Login{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
