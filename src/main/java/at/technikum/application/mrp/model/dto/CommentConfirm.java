package at.technikum.application.mrp.model.dto;

import java.util.UUID;

public class CommentConfirm {
    UUID ratingId;
    String creatorName;

    //SETTER
    public void setRatingId(UUID ratingId) {
        this.ratingId = ratingId;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    //GETTER
    public UUID getRatingId() {
        return ratingId;
    }

    public String getCreatorName() {
        return creatorName;
    }
}
