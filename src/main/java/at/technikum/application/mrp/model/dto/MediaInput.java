package at.technikum.application.mrp.model.dto;

import at.technikum.application.mrp.model.Genre;

import java.util.List;
import java.util.UUID;

public class MediaInput {
    private UUID id;
    private String title;
    private String description;
    private String mediaType;
    private Integer releaseYear; //komplexer Datentyp, damit es auch wenn im Request nichts kommt befüllt wird und im Service die Validierung stattfinden kann
    private List<Genre> genres;
    private Integer ageRestriction;
    private String creatorName;


    public MediaInput() {};

    //SETTER

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setAgeRestriction(Integer ageRestriction) {
        if(ageRestriction < 0 || ageRestriction > 18)
        {
            throw new IllegalArgumentException("Age Restriction must be between 0 and 18");
        }
        this.ageRestriction = ageRestriction;
    }

    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    //GETTER
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaType() {
        return mediaType;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Integer getAgeRestriction() {
        return ageRestriction;
    }

    public String getCreatorName() {
        return creatorName;
    }

    //Validator -> void throws exception if not correct
    public void validateMediaInput(MediaInput mediaDTO)
    {
        if (mediaDTO.getTitle() == null || mediaDTO.getTitle().isBlank()
                || mediaDTO.getDescription() == null || mediaDTO.getDescription().isBlank()
                || mediaDTO.getReleaseYear() == null
                || mediaDTO.getAgeRestriction() == null
                || mediaDTO.getCreatorName() == null || mediaDTO.getCreatorName().isBlank()
        ) {
            throw new IllegalArgumentException("MediaEntry does not contain all neccessary fields");
        }
        //Genre validieren -> Werden in MediaInput als Genre gespeichert, ist somit fix einer der erlaubten Begriffe

        //mediaType validieren
        if (!"series".equals(mediaDTO.getMediaType()) &&
                !"movie".equals(mediaDTO.getMediaType()) &&
                !"game".equals(mediaDTO.getMediaType())) {
            throw new IllegalArgumentException("mediaType can only be series, movie or game");
        }

        //releaseYear validieren
        int thisYear = java.time.Year.now().getValue();
        if(mediaDTO.getReleaseYear() < 0 || mediaDTO.getReleaseYear() > thisYear) {
            throw new IllegalArgumentException("releaseYear must be between 0 and thisYear");
        }
    }


}
