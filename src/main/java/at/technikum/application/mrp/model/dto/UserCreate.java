package at.technikum.application.mrp.model.dto;

public class UserCreate extends Dto{
    private String username;
    private String password;

    public UserCreate() {}

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
