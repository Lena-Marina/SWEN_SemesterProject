package at.technikum.application.mrp.model.dto;

import java.util.UUID;

public class LikedBy {
    UUID ratingId;
    String senderName;

    //SETTER
    public void setRatingId(UUID ratingId) {
        this.ratingId = ratingId;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    //GETTER
    public UUID getRatingId() {
        return ratingId;
    }

    public String getSenderName() {
        return senderName;
    }
}
