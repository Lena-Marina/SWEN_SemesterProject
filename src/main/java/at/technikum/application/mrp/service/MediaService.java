package at.technikum.application.mrp.service;

import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.dto.MediaInput;
import at.technikum.application.mrp.model.dto.MediaQuery;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.model.dto.RecommendationRequest;
import at.technikum.application.mrp.repository.MediaRepository;

import java.util.ArrayList;
import java.util.List;

public class MediaService {

    private MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Media> getRecommendation(RecommendationRequest dto)
    {
       //just for now - Mockdaten/ später unterscheidung nach type und weitergabe an Repo:
        List<Media> recommendations = new ArrayList<>();
        recommendations.add(new Media("1234", "Mein Freund Harvey", "movie", 1950,12 ));
        recommendations.add(new Media("12345", "Ame & Yuki", "movie", 2012, 12));

        return recommendations;
    }

    public void markAsFavorite(String id)
    {
        //schauen ob Media Id existieren
        //vermutlich auch eher das markierte Media zurückgeben

    }

    public void unmarkAsFavorite(String id)
    {
        //schauen ob Media id existiert
        //vermutlich auch eher das markierte Media zurückgeben
    }

    public void createRating(RatingInput ratingInput)
    {
        //DTO validieren

        //Repo-funktion aufrufen

        //erstelltes Rating oder bewertetes Media zurückgeben
    }

    public List<Media> getAllMedia(MediaQuery mediaQuery){
        //DTO validieren

        //fake Liste returnieren
        List<Media> filteredList = new ArrayList<>(); //stattdessn Repo-Funktion aufrufen
        filteredList.add(new Media("1234", "Mein Freund Harvey", "movie", 1950,12 ));
        filteredList.add(new Media("12345", "Ame & Yuki", "movie", 2012, 12));

        //Liste validieren

        return filteredList;
    }

    public Media updateMedia(MediaInput mediaDTO){

        //DTO validieren

        //Repo-Funktion aufrufen
        Media media = new Media(); //eigentlich mittels repository zurückbekommen!

        //for now echot es nur was es rein bekommen hat
        media.setId(mediaDTO.getId());
        media.setTitle(mediaDTO.getTitle());
        media.setDescription(mediaDTO.getDescription());
        media.setAgeRestriction(mediaDTO.getAgeRestriction());
        media.setReleaseYear(mediaDTO.getReleaseYear());

        //media validieren

        return media;
    }

    public Media deleteMedia(String mediaID) {
        //id validieren

        Media deletedMedia = new Media(); //Repofunktion aufrufen
        deletedMedia.setId(mediaID);
        deletedMedia.setTitle("Das gelöschte Medium");

        //deletedMedia validieren

        return deletedMedia;
    }

    public Media getMediaByID(String mediaID) {
        //mediaID validieren


        //RepoFunktion aufrufen
        Media media = new Media();
        media.setId(mediaID);
        media.setTitle("Media mit ID " + mediaID);

        //media Validieren

        return media;
    }

    public Media createMedia(MediaInput mediaDTO) {

        //DTO validieren

        //Repo funktion aufrufen
        Media media = new Media(); //eigentlich mittels repository zurückbekommen!

        //for now echot es nur was es rein bekommen hat
        media.setId(mediaDTO.getId());
        media.setTitle(mediaDTO.getTitle());
        media.setDescription(mediaDTO.getDescription());
        media.setAgeRestriction(mediaDTO.getAgeRestriction());
        media.setReleaseYear(mediaDTO.getReleaseYear());

        //media validieren

        return media;
    }
}
