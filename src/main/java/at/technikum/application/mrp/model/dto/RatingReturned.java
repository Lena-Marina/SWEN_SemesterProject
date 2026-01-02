package at.technikum.application.mrp.model.dto;

import java.util.UUID;

public class RatingReturned {
    private UUID ratingId;
    private int stars;
    private String comment;
    private boolean confirmed;

    private UUID creatorId;
    private UUID mediaId;

    //SETTER
    public void setRatingId(UUID ratingId) {
        this.ratingId = ratingId;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public UUID getMediaId() {
        return mediaId;
    }
}
