package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.dto.MediaInput;
import at.technikum.application.mrp.model.dto.MediaQuery;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf
 nicht nur id aus dem Pfad, sondern auch Infos aus dem Body!*/
public class MediaController extends Controller {

    private MediaService mediaService;
    private UserService userService;

    public MediaController(MediaService mediaService, UserService userService) {

        this.mediaService = mediaService;
        this.userService = userService;
    }

    public Response readAll(Request request) {

        MediaQuery mediaQuery = new MediaQuery();
        if(request.getQueryParams() != null) {
            mediaQuery.setTitle(request.getQueryParams().get("title"));
            mediaQuery.setGenre(request.getQueryParams().get("genre"));
            mediaQuery.setMediaType(request.getQueryParams().get("mediaType"));
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
        List<Media> filteredMedia = this.mediaService.getAllMedia(mediaQuery);

        //return new Response(Status.OK, ContentType.TEXT_PLAIN, "funktion read() im MediaController mit der mediaId: " + mediaID + " aufgerufen");
        //bis bessere Lösung gefunden
        return listToJson(filteredMedia, Status.OK);
    }


    public Response read(Request request) {
        String mediaID = request.extractIdAsString();

        Media media = this.mediaService.getMediaByID(mediaID);

        //media validieren

        return json(media, Status.OK);
    }


    public Response create(Request request) {
        // Request -> DTO
        MediaInput  mediaInput = toObject(request.getBody(),  MediaInput.class);

        //Namen des Creators aus dem auth-Header extrahieren
        mediaInput.setCreatorName(request.extractNameFromHeader());

        //DTO an service weitergeben
        Media createdMedia = mediaService.createMedia(mediaInput);

        //createdMedia validieren

        //just for now:
        return this.json(createdMedia, Status.CREATED);
    }


    public Response update(Request request) {
        MediaInput  mediaInput = toObject(request.getBody(),  MediaInput.class);


        //DTO an Service weitergeben
        Media updatedMedia = mediaService.updateMedia(mediaInput);

        //updatedMedia validieren -> Nein im Service!


        return json(updatedMedia, Status.OK);

    }


    public Response delete(Request request) {
        //id extrahieren
        String mediaID = request.extractIdAsString();

        //ServiceFunktion aufrufen
        Media deletedMedia = this.mediaService.deleteMedia(mediaID);

        //deletedMedia validieren

        //und erneut war ich ein Trottel, weil 204 keinen Body hat, aber ich lasse es mal so, weil es für mich
        //mehr sinn macht, das gelöschte Medium zurückzugeben, falls die Userin das Löschen rückgängig machen will.
        return json(deletedMedia, Status.UNMARKED);
    }


    public Response markAsFavourite(Request request) {
        //id extrahieren
        String id = request.extractIdAsString();

        //Service Funktion aufrufen.
        this.mediaService.markAsFavorite(id);

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Media with id " + id +" marked as favourite - Du hast die Funktion markAsFavourite() im MediaController erreicht");
    }

    public Response unmarkAsFavourite(Request request) {
        //id  extrahieren
        String id = request.extractIdAsString();

        //Service Funktion aufrufen
        this.mediaService.unmarkAsFavorite(id);

        //just for now:
        return new Response(Status.UNMARKED, ContentType.TEXT_PLAIN, "Media with id " + id +" unmarked as favorite Du hast die Funktion unmarkAsFavourite() im MediaController erreicht");
    }

    public Response rate(Request request) {
        //Dto erstellen
        RatingInput rating_dto = toObject(request.getBody(), RatingInput.class);

        //id setzen
        rating_dto.setMediaId(request.extractIdAsString());

        //DTO an Service weitergeben.
        this.mediaService.createRating(rating_dto);

        //just for now:
        return new Response(Status.CREATED, ContentType.TEXT_PLAIN, "Rating Submitted");
    }
}