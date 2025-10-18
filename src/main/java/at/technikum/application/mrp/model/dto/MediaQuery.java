package at.technikum.application.mrp.model.dto;

public class MediaQuery {
    String title;
    String genre;
    String mediaType;
    int releaseYear;
    int ageRestriction;
    int rating;
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
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public void setSortBy(String sortBy) {
        if(sortBy != "title" && sortBy != "year" && sortBy != "score" && sortBy != null)
        {
            //später durch eigene Exception austauschen
            throw new RuntimeException("Invalid sort by");
        }
        this.sortBy = sortBy;
    }


}
