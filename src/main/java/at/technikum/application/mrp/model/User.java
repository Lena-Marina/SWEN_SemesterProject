package at.technikum.application.mrp.model;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id; /* wir dürfen / sollen diese in der Applikations-Logik erstellen (Services?)*/
    private String username;
    private String hashedPassword; /*Passwort darf Klartext sein!*/
    private String email;
    private String favoriteGenre;

    private List<Media> favorites;
    private List<Rating> ratings;
    private List<Media> recommendations;

    public User(
                String username,
                String password,
                String email,
                String favoriteGenre)
    {
        this.username = username;
        this.hashedPassword = password;
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

}
