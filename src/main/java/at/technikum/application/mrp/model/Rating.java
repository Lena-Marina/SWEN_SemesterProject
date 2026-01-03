package at.technikum.application.mrp.model;


import at.technikum.application.mrp.model.dto.LikedBy;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.UUID;

public class Rating {
    private UUID ratingId;
    private int stars;       // 1-5
    private String comment;
    private boolean confirmed;  // Kommentare müssen bestätigt werden

    private UUID creatorId;       // Wer hat die Bewertung abgegeben

    @JsonIgnore   //ich brauche die Annotation, da ich sonst beim serialisieren in eine Endlosschleife komme -> ein Media hat Ratings und ein Rating hat ein Media
    private Media media;     // Auf welchen MediaEntry sich die Bewertung bezieht

    private List<UUID> likedByList;

    // SETTER
    public void setRatingId(UUID id){
            this.ratingId=id;
        }

    public void setStars(int stars) {
            if(stars < 0 || stars > 5){
                throw new IllegalArgumentException("stars must be between 0 and 5");
            }
            this.stars = stars;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setCreatorId(UUID userId) {
        this.creatorId = userId;
    }

    public void setMedia(Media deletedMedia) {
        this.media = deletedMedia;
    }

    public void setLikedByList(List<UUID> likedByList) {
        this.likedByList = likedByList;
    }

    //GETTER
    public UUID getRatingId() {
        return ratingId;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public boolean getConfirmed() {
        return confirmed;
    }

    public UUID getCreatorID() {
        return creatorId;
    }

    public Media getMedia() {
        return media;
    }

    public List<UUID> getLikedByList() {
        return likedByList;
    }
}


