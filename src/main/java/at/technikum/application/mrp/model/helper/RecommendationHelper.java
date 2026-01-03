package at.technikum.application.mrp.model.helper;

public class RecommendationHelper {
    String name;
    Integer stars;

    //SETTER

    public void setName(String name) {
        this.name = name;
    }
    public void setStars(Integer stars) {
        this.stars = stars;
    }

    //GETTER
    public String getName(){
        return name;
    }
    public Integer getStars(){
        return stars;
    }

}
