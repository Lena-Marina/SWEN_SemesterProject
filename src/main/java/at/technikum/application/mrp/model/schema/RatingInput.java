package at.technikum.application.mrp.model.schema;

public class RatingInput {
    private int stars;
    private String comment;

    public void setStars(int stars) {
        if(stars > 0 && stars < 6)
        {
            this.stars = stars;
        }
        //was sonst?
    }

}
