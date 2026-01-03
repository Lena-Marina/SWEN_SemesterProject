package at.technikum.application.mrp.model.helper;

public class RatingStatistic {
    float numberOfRatings;
    float sumOfStars;

    //SETTER
    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }
    public void setSumOfStars(int sumOfStars) {
        this.sumOfStars = sumOfStars;
    }

    public void setSumOfStars(float sumOfStars) {
        this.sumOfStars = sumOfStars;
    }

    public void setNumberOfRatings(float numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    //GETTER
    public float getNumberOfRatings() {
        return numberOfRatings;
    }
    public float getSumOfStars() {
        return sumOfStars;
    }

}
