package at.technikum.application.mrp.model.dto;

import java.util.UUID;

public class RatingInput {
    UUID mediaId;
    int stars;
    String comment;
    String creatorName;

    public RatingInput()
    {

    }

    //SETTER
    public void setMediaId(UUID mediaId)
    {
        this.mediaId = mediaId;
    }

    public void setStars(int stars)
    {
        if(stars > 0 && stars < 6 )
        {
            this.stars = stars;
        }
        else{
            throw new IllegalArgumentException("stars can't be less than 1 or more than 5");
        }
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void setCreatorName(String creatorName){
        this.creatorName = creatorName;
    }

    //GETTER
    public UUID getMediaId()
    {
        return mediaId;
    }

    public Integer getStars() { return stars; }

    public String getComment() { return comment; }

    public String getCreatorName(){ return  creatorName; }
}
