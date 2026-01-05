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
import at.technikum.application.mrp.model.helper.RatingStatistic;
import at.technikum.application.mrp.model.helper.RecommendationHelper;
import at.technikum.application.mrp.repository.FavoriteRepository;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;
import at.technikum.application.mrp.service.util.RatingValidator;

import java.util.*;
import java.util.stream.Collectors;

public class MediaService {

    private MediaRepository mediaRepository;
    private UserRepository userRepository;
    private FavoriteRepository favoriteRepository;
    private RatingRepository ratingRepository;
    private RatingValidator ratingValidator;

    public MediaService(MediaRepository mediaRepository,
                        UserRepository userRepository,
                        FavoriteRepository favoriteRepository,
                        RatingRepository ratingRepository,
                        RatingValidator ratingValidator) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
        this.ratingValidator = ratingValidator;
    }

    protected void checkPermission(UUID mediaID, String requesterName) {
        // deleter/ requester ID über Name ermitteln
        UUID requesterID = userRepository.getIdViaName(requesterName);

        // creator ID über MediaID ermitteln
        UUID creatorID = mediaRepository.getCreatorIdViaMediaEntryID(mediaID);

        if (!requesterID.equals(creatorID)) {
            throw new UnauthorizedException("Only the creator of a media entry may perform this action");
        }
    }

    public List<Media> getRecommendation(RecommendationRequest dto) {
        // Validation
        if(dto.getType() == null){
            throw new IllegalArgumentException("RecommendationRequest must have Queryparameter 'type'");
        }
        if (!"genre".equals(dto.getType()) && !"content".equals(dto.getType())) {
            throw new IllegalArgumentException("QueryParam 'type' for recommendations has to be either 'genre' or 'content'");
        }

        //Alle Ratings von dem user finden
        List<Rating> userRatings = this.ratingRepository.findAllFrom(dto.getUserId());

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

        // "" aus comments der Ratings entfernen, wenn !confirmed
        for(Media media : recommendations){
            ratingValidator.removeCommentsIfNotConfirmedFromMedia(media);
        }

        return recommendations;
    }

    public void markAsFavorite(UUID mediaID, String username) {
        // user_id herausfinden
        UUID userID = userRepository.getIdViaName(username);

        if (userID == null) {
            throw new IllegalArgumentException(username + " not in DB");
        }

        this.favoriteRepository.markAsFavorite(mediaID, userID);

    }

    public void unmarkAsFavorite(UUID mediaID, String username) {
        //user_id herausfinden
        UUID userID = userRepository.getIdViaName(username);

        if (userID == null) {
            throw new IllegalArgumentException(username + " not in DB");
        }

        this.favoriteRepository.unMarkAsFavorite(mediaID, userID);

    }

    public List<Media> getFilteredMedia(MediaQuery mediaQuery) {
        //DTO Validieren? -> es darf ja alles Null sein, ich könnte prüfen ob die Strings "" sind
        if ("".equals(mediaQuery.getTitle()) ||
                "".equals(mediaQuery.getMediaType()) ||
                "".equals(mediaQuery.getGenre()) ||
                "".equals(mediaQuery.getSortBy())) {
            throw new IllegalArgumentException("Empty Strings are not allowed as QueryParams");
        }
        // muss validieren, dass mediaQuery.getGenre() einem akzeptierten Genre entspricht da mediaQuery das genre als String speichert
        String genreStr = mediaQuery.getGenre();
        if (genreStr != null) {
            try {
                Genre.valueOf(genreStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid genre: " + genreStr);
            }
        }
        // muss validieren, dass mediaQuery.getMediaType() einem der akzeptierten mediaTypes entpricht
        String mediaType = mediaQuery.getMediaType();
        if (mediaType != null &&
                !mediaType.equals("movie") &&
                !mediaType.equals("series") &&
                !mediaType.equals("game")) {
            throw new IllegalArgumentException("Invalid media type: " + mediaType);
        }
        // muss validieren, dass mediaQuery.getReleaseYear() ein valides Jahr ist (>= 0 && < this.year?)
        Integer releaseYear = mediaQuery.getReleaseYear();
        int thisYear = java.time.Year.now().getValue();
        if (releaseYear != null && (releaseYear < 0 || releaseYear > thisYear)) {
            throw new IllegalArgumentException("Invalid release year (0-" + thisYear + "): " + releaseYear);
        }
        // muss validieren, dass mediaQuery.getAgeRestriction() eine valide AgeRestriction ist
        Integer ageRestriction = mediaQuery.getAgeRestriction();
        if (ageRestriction != null && (ageRestriction < 0 || ageRestriction > 18)) {
            throw new IllegalArgumentException("Invalid age restriction (0-18): " + ageRestriction);
        }
        // muss validieren, dass mediaQuery.getRating() eine valides Rating ist (entspricht star value)
        Number rating = mediaQuery.getRating();
        if (rating != null && (rating.intValue() < 1 || rating.intValue() > 5)) {
            throw new IllegalArgumentException("Invalid rating (1-5): " + rating);
        }
        //sort by auch auf seine erlaubten Werte validieren
        String sortBy = mediaQuery.getSortBy();
        if (sortBy != null && !List.of("title", "year", "score").contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }

        /* Im Repository soll der SQL String aufgebaut werden, sodass er nur nach jenen Params filtered die nicht Null sind.
         * Diese Funktion belasse ich im Repository, da wir ja wollen, dass die höheren Schichten nichts über SQL wissen
         * Für den Fall, dass in Zukunft die Datenbank ausgewechselt werden soll.
         *
         * den Average Score habe ich bisher im Service berechnet
         * -> es müsste aber theoretisch möglich sein bei der Abfrage eine Spalte dazuzugeben
         * welche diesen enthält
         *
         * Was passiert dann noch hier im Service?
         * -> Commentare in den ratings auf "" setzen, wenn nicht confirmed
         * */

        List<Media> filteredMediaList = this.mediaRepository.findFiltered(mediaQuery);

        //Kommentare in den Ratings der Medias auf "" setzen, wenn !confirmed
        for(Media media : filteredMediaList) {
            ratingValidator.removeCommentsIfNotConfirmedFromMedia(media);
        }

        return filteredMediaList;
    }

    public Media updateMedia(MediaInput mediaDTO) {
        /*Ich hätte es genrell so verstanden, dass alle Informationen die ab jetzt
         * in der DB sein sollen mit dem Request geliefert werden - nicht nur Änderungen
         * Dass also auch alte Informationen wieder mitgeschickt werden.
         * (z.B. Wenn der MediaEntry früher das Genre HORROR hatte
         * und jetzt das Genre COMEDY ergänzt werden soll,
         * enthält der Request ein Array, dass sowohl HORROR als auch COMEDY enthält
         * Daher: ich habe alle alten genre-verknüpfungen gelöscht und neue erstellt
         *
         * Ich prüfe also NICHT, was bereits in der DB vorhanden ist und ergänze nur,
         * da ich annehme, dass auch Genres entfernt werden können.*/
        //DTO validieren
        mediaDTO.validateMediaInput(mediaDTO);

        //Prüfen ob Anfragesteller:in authorisiert ist
        checkPermission(mediaDTO.getId(), mediaDTO.getCreatorName());

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
        if (!updatedMediaOpt.isPresent()) {
            throw new EntityNotSavedCorrectlyException("saved media was not returned from DB");
        }

        Media updatedMedia = updatedMediaOpt.get();

        ratingValidator.removeCommentsIfNotConfirmedFromMedia(updatedMedia);

        return updatedMedia;
    }

    public Media deleteMedia(UUID mediaID, String deleterName) {

        //MediaID validieren -> Ist es eine valide UUID? --> muss sein, denn sonst hätte ich sie nicht als UUID entgegen nehmen können

        //Prüfen ob Anfragesteller:in authorisiert ist
        checkPermission(mediaID, deleterName);

        Media deletedMedia = this.mediaRepository.delete(mediaID); //Repofunktion aufrufen

        ratingValidator.removeCommentsIfNotConfirmedFromMedia(deletedMedia);
        //Validation -> was gehört hier geprüft? Basis Sachen dürfen nicht leer sein, Listen schon
        // die andere Frage ist, was bedeutet es, wenn hier etwas unvollständig ist?
        // ich will keine Exception werfen, denn dann ist alles verloren.
        // wenn ein Teil der Informationen beim Löschen verloren gegangen ist,
        // ist das nicht immer noch besser als wenn alle verloren gegangen sind?

        return deletedMedia;
    }

    public Media getMediaByID(UUID mediaID) {

        //RepoFunktion aufrufen
        Optional<Media> mediaOpt = this.mediaRepository.find(mediaID);

        //media Validieren
        if (!mediaOpt.isPresent()) {
            throw new EntityNotSavedCorrectlyException("no media found");
        }

        Media media = mediaOpt.get();

        //Kommentare in Ratings löschen, wenn nicht freigegeben
        ratingValidator.removeCommentsIfNotConfirmedFromMedia(media);

        //weitere Validation?

        //Average Score berechnen -> Business "Logik" deswegen hier und nicht schon in find()
        media.setAverageScore(this.getAverageScore(mediaID));

        return media;
    }

    private Float getAverageScore(UUID mediaID) {
        RatingStatistic rst = this.ratingRepository.getRatingStatistic(mediaID);

        return rst.getSumOfStars() / rst.getNumberOfRatings();
    }

    public Media createMedia(MediaInput mediaDTO) {

        //DTO validieren -> wirft exception wenn ein Fehler darin ist
        mediaDTO.validateMediaInput(mediaDTO);

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
        if (!createdMediaOpt.isPresent()) {
            throw new EntityNotSavedCorrectlyException("saved media was not returned from DB");
        }

        Media createdMedia = createdMediaOpt.get();

        if (createdMedia.getId() == null
                || createdMedia.getCreatorID() == null
                || createdMedia.getTitle() == null || createdMedia.getTitle().isBlank()
                || createdMedia.getDescription() == null || createdMedia.getDescription().isBlank()
                || createdMedia.getMediaType() == null || createdMedia.getMediaType().isBlank()
        ) {
            throw new EntityNotSavedCorrectlyException("saved Media does not contain all necessary fields");
        }

        return createdMedia;
    }



}
