package at.technikum.application.mrp.model.dto;

import at.technikum.application.mrp.model.Genre;

import java.util.UUID;

public class UserUpdate {
    private UUID userID;
    private String email;
    private Genre favoriteGenre;

    public UserUpdate(){};

    //SETTER
    public void setUserID(UUID userID){
        this.userID=userID;
    }
    public void setEmail(String eMail){
        this.email = eMail;
    }
    public void setFavoriteGenre(Genre favGenre){
        this.favoriteGenre = favGenre;
    }

    //GETTER
    public UUID getUserID() {
        return userID;
    }
    public String getEmail(){
        return email;
    }
    public Genre getFavoriteGenre(){
        return favoriteGenre;
    }
}
