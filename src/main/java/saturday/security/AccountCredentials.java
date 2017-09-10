package storyboard.security;

/**
 * Created by zachjustice on 7/29/17.
 */
public class AccountCredentials {
    private String email;
    private String password_hash;

    public AccountCredentials(String email, String password) {
        this.email = email;
        this.password_hash = password;
    }

    public String getPasswordHash() {
        return password_hash;
    }

    public void setPasswordHash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }
}
