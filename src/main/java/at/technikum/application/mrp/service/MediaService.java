package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.dto.MediaInput;
import at.technikum.application.mrp.model.dto.MediaQuery;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.model.dto.RecommendationRequest;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.UserRepository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaService {

    private MediaRepository mediaRepository;
    private UserRepository userRepository;

    public MediaService(MediaRepository mediaRepository,  UserRepository userRepository) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    public List<Media> getRecommendation(RecommendationRequest dto)
    {
       //just for now - Mockdaten/ später unterscheidung nach type und weitergabe an Repo:
        List<Media> recommendations = new ArrayList<>();
        //recommendations.add(new Media("1234", "Mein Freund Harvey", "movie", 1950,12 ));
        //recommendations.add(new Media("12345", "Ame & Yuki", "movie", 2012, 12));

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
        //filteredList.add(new Media("1234", "Mein Freund Harvey", "movie", 1950,12 ));
        //filteredList.add(new Media("12345", "Ame & Yuki", "movie", 2012, 12));

        //Liste validieren

        return filteredList;
    }

    public Media updateMedia(MediaInput mediaDTO){
        /*Ich hätte es genrell so verstanden, dass alle Informationen die ab jetzt
        * in der DB sein sollen mit dem Request geliefert werden - nicht nur Änderungen
        * Dass also auch alte Informationen wieder mitgeschickt werden.
        * (z.B. Wenn der MediaEntry früher das Genre HORROR hatte
        * und jetzt das Genre COMEDY ergänzt werden soll,
        * enthält der Request ein Array, dass sowohl HORROR als auch COMEDY enthält
        * Daher: ich habe alle alten genre-verknüpfungen gelöscht und neue erstellt
        *
        * Ich prüfe also NICHT, was bereits in der DB vorhanden ist und ergänze nur,
        * das ich annehme, dass auch Genres entfernt werden können.*/
        //DTO validieren
        if(mediaDTO.getTitle() == null || mediaDTO.getTitle().isBlank()
                || mediaDTO.getDescription() == null || mediaDTO.getDescription().isBlank()
                || mediaDTO.getReleaseYear() == null
                || mediaDTO.getAgeRestriction() == null
                || mediaDTO.getCreatorName() == null || mediaDTO.getCreatorName().isBlank()
        ){
            throw new IllegalArgumentException("MediaEntry does not contain all neccessary fields");
        }

        //check ob Anfragesteller auch creator ist
        String originalCreatorName = this.mediaRepository.getCreatorNameByMediaID(mediaDTO.getId());

        //DEBUGGING
        System.out.println("---------------------------------");
        System.out.println("DEBUG Freigabe des Updatens eines Media Entrys in MediaService::updateMedia() ");
        System.out.println("DEBUG: originalCreatorName= " + originalCreatorName);
        System.out.println("DEBUG: creatorNameNow = " + mediaDTO.getCreatorName());

        if(!(originalCreatorName.equals(mediaDTO.getCreatorName())))
        {
            throw new UnauthorizedException("users can only edit their own Media_Entrys");
        }

        //Media Objekt aus DTO erstellen
        Media media = new Media();
        media.setId(mediaDTO.getId());
        media.setTitle(mediaDTO.getTitle());
        media.setDescription(mediaDTO.getDescription());
        media.setReleaseYear(mediaDTO.getReleaseYear());
        media.setAgeRestriction(mediaDTO.getAgeRestriction());
        media.setMediaType(mediaDTO.getMediaType());
        media.setGenres(mediaDTO.getGenres());
        //Creator_ID ermitteln
        UUID creatorId = this.userRepository.getIdViaName(mediaDTO.getCreatorName());
        media.setCreatorID(creatorId);

        //MediaEntry mittels Repo-Funktion updaten
        Optional<Media> updatedMediaOpt = this.mediaRepository.update(media);

        //geupdatetes Media validieren -> Optional entpacken usw
        //media validieren
        if(!updatedMediaOpt.isPresent()){
            throw new EntityNotSavedCorrectlyException("saved media was not returned from DB");
        }

        Media updatedMedia = updatedMediaOpt.get();

        if(updatedMedia.getId() == null
                || updatedMedia.getCreatorID() == null
                || updatedMedia.getTitle() == null || updatedMedia.getTitle().isBlank()
                || updatedMedia.getDescription() == null || updatedMedia.getDescription().isBlank()
                || updatedMedia.getMediaType() == null || updatedMedia.getMediaType().isBlank()
        )
        {
            throw new EntityNotSavedCorrectlyException("updated Media does not contain all necessary fields");
        }

        return updatedMedia;
    }

    public Media deleteMedia(UUID mediaID, String deleterName) {

        //MediaID validieren -> Ist es eine valide UUID? --> muss sein, denn sonst hätte ich sie nicht als UUID entgegen nehmen können

        //deleterID über namen herausfinden
        UUID deleterID = this.userRepository.getIdViaName(deleterName);

        //creatorID über mediaID herausfinden
        UUID creatorID = this.mediaRepository.getCreatorIdViaMediaEntryID(mediaID);

        //deleterID und creatirID müssen gleich sein
        if(!deleterID.equals(creatorID))
        {
            throw new UnauthorizedException("only the creator of a mediaEntry may destroy it");
        }


        Media deletedMedia = this.mediaRepository.delete(mediaID); //Repofunktion aufrufen

        //Validation -> was gehört hier geprüft? Basis Sachen dürfen nicht leer sein, Listen schon
        // die andere Frage ist, was bedeutet es, wenn hier etwas unvollständig ist?
        // ich will keine Exception werfen, denn dann ist alles verloren.
        // wenn ein Teil der Informationen beim Löschen verloren gegangen ist,
        // ist das nicht immer noch besser als wenn alle verloren gegangen sind?

        return deletedMedia;
    }

    public Media getMediaByID(String mediaID) {
        //mediaID validieren


        //RepoFunktion aufrufen
        Media media = new Media();
        //media.setId(mediaID);
        media.setTitle("Media mit ID " + mediaID);

        //media Validieren

        return media;
    }

    public Media createMedia(MediaInput mediaDTO) {
        //DTO validieren
        if(mediaDTO.getTitle() == null || mediaDTO.getTitle().isBlank()
                || mediaDTO.getDescription() == null || mediaDTO.getDescription().isBlank()
                || mediaDTO.getReleaseYear() == null
                || mediaDTO.getAgeRestriction() == null
                || mediaDTO.getCreatorName() == null || mediaDTO.getCreatorName().isBlank()
        ){
            throw new IllegalArgumentException("MediaEntry does not contain all neccessary fields");
        }
        //Genre validieren? Braucht es ein Genre oder darf es auch keines haben? Ich denke es darf keines haben, dann taucht es halt bei Suchen nach Genre nicht auf

        //Media_ID dazugeben
        mediaDTO.setId(UUID.randomUUID());

        //Creator_ID ermitteln
        UUID creatorId = this.userRepository.getIdViaName(mediaDTO.getCreatorName());


        //DEBUGGING
        System.out.println("---------------------------------");
        System.out.println("DEBUG erhaltenes DTO in MediaService::createMedia() ");
        System.out.println("DEBUG: ID= " + mediaDTO.getId());
        System.out.println("DEBUG: title = " + mediaDTO.getTitle());
        System.out.println("DEBUG: description = " + mediaDTO.getDescription());
        System.out.println("DEBUG: mediaType= " + mediaDTO.getMediaType());
        System.out.println("DEBUG: releaseYear= " + mediaDTO.getReleaseYear());
        System.out.println("DEBUG: Genres = " + mediaDTO.getGenres());
        System.out.println("DEBUG: AgeRestriction = " + mediaDTO.getAgeRestriction());
        System.out.println("DEBUG: CreatorID = " + creatorId);

        //Repo Funktion erwartet ein Media Objekt, nicht das DTO (Weil ich ja mit Templates gearbeitet habe, lässt sich das jetzt auch nicht so leicht ändern
        Media media = new Media();
        media.setId(mediaDTO.getId());
        media.setTitle(mediaDTO.getTitle());
        media.setDescription(mediaDTO.getDescription());
        media.setReleaseYear(mediaDTO.getReleaseYear());
        media.setAgeRestriction(mediaDTO.getAgeRestriction());
        media.setMediaType(mediaDTO.getMediaType());
        media.setGenres(mediaDTO.getGenres());
        media.setCreatorID(creatorId);


        //Repo funktion aufrufen
        Optional<Media> createdMediaOpt = this.mediaRepository.create(media);

        //media validieren
        if(!createdMediaOpt.isPresent()){
            throw new EntityNotSavedCorrectlyException("saved media was not returned from DB");
        }

        Media createdMedia = createdMediaOpt.get();

        if(createdMedia.getId() == null
                || createdMedia.getCreatorID() == null
                || createdMedia.getTitle() == null || createdMedia.getTitle().isBlank()
                || createdMedia.getDescription() == null || createdMedia.getDescription().isBlank()
                || createdMedia.getMediaType() == null || createdMedia.getMediaType().isBlank()
        )
        {
            throw new EntityNotSavedCorrectlyException("saved Media does not contain all necessary fields");
        }

        return createdMedia;
    }

    public List<Media> getUsersFavourites(String userId) {

        //fake Liste returnieren
        List<Media> favourites = new ArrayList<>(); //stattdessn Repo-Funktion aufrufen
        //favourites.add(new Media("1234", "Mein Freund Harvey", "movie", 1950,12 ));
        //favourites.add(new Media("12345", "Ame & Yuki", "movie", 2012, 12));

        return favourites;

    }
}
