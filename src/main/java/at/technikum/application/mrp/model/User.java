package at.technikum.application.mrp.model;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id; /* wir dürfen / sollen diese in der Applikations-Logik erstellen (Services?)*/
    private String username;
    private String password; /*Passwort darf Klartext sein!*/
    private String email;
    private String favoriteGenre;

    private List<Media> favorites;
    private List<Rating> ratings;
    private List<Media> recommendations;

    public User () {

    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public User(
                String username,
                String password,
                String email,
                String favoriteGenre)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.favoriteGenre = favoriteGenre;
    }

    //Getter und Setter
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        //validationen?
        this.username = username;
    }
    public void setPassword(String password) {
        //validationen?
        this.password = password;
    }

}
