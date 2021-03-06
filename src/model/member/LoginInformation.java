package model.member;

/**
 * Class for holding login information.
 * @author David Lerner
 */
public class LoginInformation {
    private String email;
    private String password;

    public LoginInformation(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //only equal when both fields are
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LoginInformation))
            return false;
        LoginInformation li = (LoginInformation)o;
        return (li.getEmail().equals(email) && li.getPassword().equals(password));
    }
    
}
