package at.technikum.application.mrp.model.dto;

public class UserUpdate {
    private String userID;
    private String eMail;
    private String favGenre;

    public UserUpdate(){};

    //SETTER
    public void setUserID(String userID){
        this.userID=userID;
    }
    public void setEmail(String eMail){
        this.eMail = eMail;
    }
    public void setFavoriteGenre(String favGenre){
        this.favGenre = favGenre;
    }

    //GETTER
    public String getUserID() {
        return userID;
    }
    public String getEmail(){
        return eMail;
    }
    public String getFavoriteGenre(){
        return favGenre;
    }
}
