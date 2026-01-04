package at.technikum.application.mrp.model.dto;

public class MediaQuery {
    String title;
    String genre;
    String mediaType;
    Integer releaseYear; //ich brauche hier Wrapper, damit ich unterscheiden kann zwischen: Param nicht gesetzt (Null) und releaseYear = 0
    Integer ageRestriction;
    Integer rating;
    String sortBy;

    //SETTER
    public void setTitle(String title) {
        this.title = title;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
    public void setAgeRestriction(Integer ageRestriction) {
        this.ageRestriction = ageRestriction;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    //GETTER
    public String getTitle() {
        return title;
    }
    public String getGenre() {
        return genre;
    }
    public String getMediaType() {
        return mediaType;
    }
    public Integer getReleaseYear() {
        return releaseYear;
    }
    public Integer getAgeRestriction() {
        return ageRestriction;
    }
    public Integer getRating() {
        return rating;
    }
    public String getSortBy() {
        return sortBy;
    }


}
