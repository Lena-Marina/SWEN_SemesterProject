package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.MediaInput;
import at.technikum.application.mrp.model.dto.MediaQuery;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.model.dto.RecommendationRequest;
import at.technikum.application.mrp.model.helper.RecommendationHelper;
import at.technikum.application.mrp.repository.FavoriteRepository;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class MediaService {

    private MediaRepository mediaRepository;
    private UserRepository userRepository;
    private FavoriteRepository favoriteRepository;
    private RatingRepository ratingRepository;

    public MediaService(MediaRepository mediaRepository,
                        UserRepository userRepository,
                        FavoriteRepository favoriteRepository,
                        RatingRepository ratingRepository) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<Media> getRecommendation(RecommendationRequest dto) {
        // Validation
        if (!"genre".equals(dto.getType()) && !"content".equals(dto.getType())) {
            throw new IllegalArgumentException("QueryParam 'type' for recommendations has to be either 'genre' or 'content'");
        }

        //Alle Ratings von dem user finden
        List<Rating> userRatings = this.ratingRepository.findAllFrom(dto.getUserId());
        //DEBUGGING
        System.out.println("DEBUGGING: in MediaService::getRecommendation() List<Rating> userRatings: ");
        for (Rating rating : userRatings) {
            System.out.println(rating.getRatingId());
        }

        //Je nach type eine andere Repo Funktion aufrufen, um recommendations zu befüllen (Weil Logik, will ich das hier und nicht im Repo)
        List<RecommendationHelper> recoms = new ArrayList<>();
        if (dto.getType().equals("content")) {

            for (Rating rating : userRatings) {
                RecommendationHelper recom = this.ratingRepository.getTypeWithStars(rating.getRatingId());
                recoms.add(recom);
            }

        } else if (dto.getType().equals("genre")) {
            for (Rating rating : userRatings) {
                List<RecommendationHelper> recomList = this.ratingRepository.getGenresWithStars(rating.getRatingId());
                recoms.addAll(recomList);
            }
        }
        //DEBUGGING
        System.out.println("DEBUGGING: in MediaService::getRecommendation() List<RecommendationHelper> recoms: ");
        for (RecommendationHelper recom : recoms) {
            System.out.println(recom.getName());
        }

        //auswerten was insgesamt die meisten Sterne hat
        Map<String, Integer> starsPerName = new HashMap<>();
        for (RecommendationHelper recom : recoms) {
            starsPerName.merge(recom.getName(), recom.getStars(), Integer::sum);
        }
        //DEBUGGING
        System.out.println("DEBUGGING: in MediaService::getRecommendation() Map<String, Integer> starsPerName:");
        for (Map.Entry<String, Integer> entry : starsPerName.entrySet()) {
            System.out.println("DEBUGGING:   Name: " + entry.getKey() + ", Total Stars: " + entry.getValue());
        }

        // Sortieren nach höchster Summe
        List<Map.Entry<String, Integer>> sortedEntries = starsPerName.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        //DEBUGGING
        System.out.println("DEBUGGING: in MediaService::getRecommendation() List<Map.Entry<String, Integer>> sortedEntries:");
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            System.out.println("DEBUGGING:   Name: " + entry.getKey() + ", Total Stars: " + entry.getValue());
        }

        //ja nachdem ob genre oder content unterschiedliche Funktionen aufrufen um alle dazu passenden media_entrys zu bekommen
        List<Media> recommendations = new ArrayList<>();

        if (!sortedEntries.isEmpty()) {
            String topName = sortedEntries.get(0).getKey();
            //DEBUGGING
            System.out.println("DEBUGGING: in MediaService::getRecommendation() | topName: " + topName);
            System.out.println("DEBUGGING: in MediaService::getRecommendation() | type: " + dto.getType());
            if (dto.getType().equals("genre")) {
                recommendations = this.mediaRepository.findAllWithGenre(topName);
            } else if (dto.getType().equals("content")) {
                recommendations = this.mediaRepository.findAllWithType(topName);
            }
        }
        //Keine validierung für eine leere Liste -> Wenn User keine Ratings verfasst, bekommt User keine Recommendations
        // In echt hätte man hier wsl eine Basis Liste mit aktuell gepushten Titeln?

        return recommendations;
    }

    public void markAsFavorite(UUID mediaID, String username)
    {
        // user_id herausfinden
        UUID userID = userRepository.getIdViaName(username);

        if(userID == null)
        {
            throw new IllegalArgumentException(username + " not in DB");
        }

        this.favoriteRepository.markAsFavorite(mediaID, userID);

    }

    public void unmarkAsFavorite(UUID mediaID, String username)
    {
        //user_id herausfinden
        UUID userID = userRepository.getIdViaName(username);

        if(userID == null)
        {
            throw new IllegalArgumentException(username + " not in DB");
        }

        this.favoriteRepository.unMarkAsFavorite(mediaID, userID);

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

}
