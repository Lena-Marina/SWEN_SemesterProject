package at.technikum.application.mrp.model;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id; /* wir dürfen / sollen diese in der Applikations-Logik erstellen (Services?)*/
    private String username;
    private String password; /*Passwort darf Klartext sein!*/
    private String email;
    private Genre favoriteGenre;

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
                Genre favoriteGenre)
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

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    public List<Media> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Media> favorites) {
        this.favorites = favorites;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Media> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Media> recommendations) {
        this.recommendations = recommendations;
    }



}
