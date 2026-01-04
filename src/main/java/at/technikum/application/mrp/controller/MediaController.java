package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.MediaInput;
import at.technikum.application.mrp.model.dto.MediaQuery;
import at.technikum.application.mrp.model.dto.RatingReturned;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.application.mrp.service.RatingService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;
import java.util.UUID;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf
 nicht nur id aus dem Pfad, sondern auch Infos aus dem Body!*/
public class MediaController extends Controller {

    private MediaService mediaService;
    private UserService userService;
    private RatingService ratingService;

    public MediaController(MediaService mediaService, UserService userService, RatingService ratingService) {

        this.mediaService = mediaService;
        this.userService = userService;
        this.ratingService = ratingService;
    }

    public Response readAll(Request request) {

        MediaQuery mediaQuery = new MediaQuery();
        if(request.getQueryParams() != null) {
            mediaQuery.setTitle(request.getQueryParams().get("title"));
            mediaQuery.setGenre(request.getQueryParams().get("genre"));
            mediaQuery.setMediaType(request.getQueryParams().get("mediaType"));

            // bei den Zahlen muss ich erst schauen ob sie gesetzt wurden, da wenn ich versuche eine Zahl aus einem nicht gesetzten Param zu machen eine Exception geworfen wird
            String releaseYearParam = request.getQueryParams().get("releaseYear");
            if (releaseYearParam != null) {
                mediaQuery.setReleaseYear(Integer.parseInt(releaseYearParam));
            }
            String ageRestrictionParam = request.getQueryParams().get("ageRestriction");
            if (ageRestrictionParam != null) {
                mediaQuery.setAgeRestriction(Integer.parseInt(ageRestrictionParam));
            }
            String ratingParam = request.getQueryParams().get("rating");
            if (ratingParam != null) {
                mediaQuery.setRating(Integer.parseInt(ratingParam));
            }
            mediaQuery.setSortBy(request.getQueryParams().get("sortBy"));

        }

        //Funktion des Service aufrufen
        List<Media> filteredMedia = this.mediaService.getFilteredMedia(mediaQuery);


        return listToJson(filteredMedia, Status.OK);
    }


    public Response read(Request request) {
        UUID mediaID = request.extractIdAsUUID();
        //jede:r darf ein Media lesen, daher muss ich creatorName nicht aus dem Header auslesen

        Media media = this.mediaService.getMediaByID(mediaID);

        return json(media, Status.OK);
    }


    public Response create(Request request) {
        // Request -> DTO
        MediaInput  mediaInput = toObject(request.getBody(),  MediaInput.class);

        //Namen des Creators aus dem auth-Header extrahieren
        mediaInput.setCreatorName(request.extractNameFromHeader());

        //DTO an service weitergeben
        Media createdMedia = mediaService.createMedia(mediaInput);


        return this.json(createdMedia, Status.CREATED);
    }


    public Response update(Request request) {
        MediaInput  mediaInput = toObject(request.getBody(),  MediaInput.class);

        //Media_ID aus dem Request ermitteln
        mediaInput.setId(request.extractIdAsUUID());

        //Name creator_ermitteln, um validieren zu können, dass die Person den Eintrag überhaupt ändern darf
        mediaInput.setCreatorName(request.extractNameFromHeader());

        //DTO an Service weitergeben
        Media updatedMedia = mediaService.updateMedia(mediaInput);

        return json(updatedMedia, Status.OK);
    }


    public Response delete(Request request) {
        //media_id extrahieren
        UUID mediaID = request.extractIdAsUUID();

        //creator_id extrahieren
        String deleterName = request.extractNameFromHeader();

        //ServiceFunktion aufrufen
        Media deletedMedia = this.mediaService.deleteMedia(mediaID, deleterName);

        //deletedMedia validieren -> Nein, Validation mache ich im Service

        // Laut Spezifikationen wird hier eigentlich 204 verlangt,
        // Aber: wir haben im Unterricht besprochen, dass es Sinn macht das gelöschte
        // wieder zurück zu schicken, falls der/die User:In das Löschen Rückgängig machen möchte
        // Daher musste ich den Status Code ändern, da 204 ja keinen Body erlaubt
        return json(deletedMedia, Status.OK);
    }


    public Response markAsFavourite(Request request) {
        //Media-id extrahieren
        UUID mediaID = request.extractIdAsUUID();

        //User-name extrahieren
        String username = request.extractNameFromHeader();

        //Service Funktion aufrufen.
        this.mediaService.markAsFavorite(mediaID, username);

        //wenn bisher keine Exception geworfen wurde positive Rückmeldung:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Media with id " + mediaID +" marked as favourite by user:  " + username);
    }

    public Response unmarkAsFavourite(Request request) {
        //media_id  extrahieren
        UUID mediaID = request.extractIdAsUUID();

        //user_id extrahieren
        String username = request.extractNameFromHeader();


        //Service Funktion aufrufen
        this.mediaService.unmarkAsFavorite(mediaID, username);

        //wenn bisher kein Fehler:
        return new Response(Status.UNMARKED, ContentType.TEXT_PLAIN, "Media with id " + mediaID +" unmarked as favorite by user: " + username);
    }

    public Response rate(Request request) {
        //DEBUGGING
        System.out.println("---------------------------------");
        System.out.println("DEBUG in MediaController::rate() ");

        //Dto erstellen
        RatingInput rating_dto = toObject(request.getBody(), RatingInput.class);

        //Media_id setzen
        rating_dto.setMediaId(request.extractIdAsUUID());

        //userNamen herausfinden
        rating_dto.setCreatorName(request.extractNameFromHeader());

        //DTO an Service weitergeben.
        Rating savedRatingDTO = this.ratingService.createRating(rating_dto);

        // JSON-Response zurückgeben mit Status 201 Created
        return json(savedRatingDTO, Status.CREATED);
    }
}