import java.io.Serializable;

public class Login implements Serializable, Comparable {
    private static final long serialVersionUID = -89187293563569244L;
    private String username;
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Login) {
            if(this.username.equals(((Login) o).getUsername()) && this.password.equals(((Login) o).getPassword()))
                return 0;
            else return -1;
        }
        else {
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
