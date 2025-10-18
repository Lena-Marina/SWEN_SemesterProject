package at.technikum.application.mrp.model.dto;

public class RatingInput {
    String mediaId;
    int stars;
    String comment;

    public RatingInput()
    {

    }

    //SETTER
    public void setMediaId(String mediaId)
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

    //GETTEr
    public String getMediaId()
    {
        return mediaId;
    }
}
