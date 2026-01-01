package at.technikum.application.mrp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Media {
    //Ids in der Datenbank und Somit auch heir sollten nicht einfach selbstinkrementierende Ints sein,
    //sondern z.B. UUIDs wir werden uns mit Strings behelfen, welche UUIDS repräsentieren
    //es gibt in der Realität auch einen Datentyp für UUIDS
        private UUID id;
        private String title;
        private String description;
        private String mediaType; // z.B. "movie", "series", "book"
        private Integer releaseYear;
        private List<Genre> genres;
        private Integer ageRestriction;
        private UUID creatorId;

        private List<UUID> favoritedBy; // UUIDS der Users, die dieses Medium als Favorit markiert haben
        private List<Rating> ratings;

        public Media(UUID id, String title, String mediaType, int releaseYear, int ageRestriction) {
            this.id = id;
            this.title = title;
            this.mediaType = mediaType;
            this.releaseYear = releaseYear;
            this.ageRestriction = ageRestriction;
            this.favoritedBy = new ArrayList<>();
            this.ratings = new ArrayList<>();
        }

        public Media()
        {

        }

    // Getter & Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public List<UUID> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(List<UUID> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public UUID getCreatorID() { return creatorId; }

    public void setCreatorID(UUID creatorID) { this.creatorId = creatorID; }

}
