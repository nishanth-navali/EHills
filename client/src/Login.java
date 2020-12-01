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

    // password encryption
    public Login encrypt() {
        StringBuilder newPassword = new StringBuilder();
        for(char c : password.toCharArray()) {
            if(c >= 'a' && c <= 'z') {
                c = (char) (((c - 'a') + 4)%26 + 'a');
            }
            else if(c >= 'A' && c <= 'Z') {
                c = (char) (((c - 'A') + 5)%26 + 'A');
            }
            else if(c <= '9' && c >= '0') {
                c = (char) (((c - '0') + 6)%10 + '0');
            }
            newPassword.append(c);
        }
        password = newPassword.toString();
        return this;
    }

    // password decryption
    public void decrypt() {
        StringBuilder newPassword = new StringBuilder();
        for(char c : password.toCharArray()) {
            if(c >= 'a' && c <= 'z') {
                c = (char) (((c - 'a') - 4)%26 + 'a');
            }
            else if(c >= 'A' && c <= 'Z') {
                c = (char) (((c - 'A') - 5)%26 + 'A');
            }
            else if(c <= '9' && c >= '0') {
                c = (char) (((c - '0') - 6)%10 + '0');
            }
            newPassword.append(c);
        }
        password = newPassword.toString();
    }

    // Comparable implementation for easy checking if the Login is valid
    @Override
    public int compareTo(Object o) {
        if (o instanceof Login) {
            if (this.username.equals(((Login) o).username) && this.password.equals(((Login) o).password))
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
