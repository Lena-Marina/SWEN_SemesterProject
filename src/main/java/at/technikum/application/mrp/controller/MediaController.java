package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf
 nicht nur id aus dem Pfad, sondern auch Infos aus dem Body!*/
public class MediaController extends Controller {

    private MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public Response readAll(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getAllMedia() im MediaController erreicht");
    }


    public Response read(Request request) {
        //Funktion des Service aufrufen
        //return new Response(Status.OK, ContentType.TEXT_PLAIN, "funktion read() im MediaController mit der mediaId: " + mediaID + " aufgerufen");


        //bis bessere Lösung gefunden
        return new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Ungueltiger Pfad: " + request.getPath());
    }


    public Response create(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion create() im MediaController erreicht");
    }


    public Response update(Request request) {
        // id extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion update() im MediaController erreicht");
    }


    public Response delete(Request request) {
        //id extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion delete() im MediaController erreicht");
    }


    public Response markAsFavourite(Request request) {
        //id extrahieren
        String id = request.extractId();

        //Service Funktion aufrufen.
        this.mediaService.markAsFavorite(id);

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Media with id " + id +" marked as favourite - Du hast die Funktion markAsFavourite() im MediaController erreicht");
    }

    public Response unmarkAsFavourite(Request request) {
        //id  extrahieren
        String id = request.extractId();

        //Service Funktion aufrufen
        this.mediaService.unmarkAsFavorite(id);

        //just for now:
        return new Response(Status.UNMARKED, ContentType.TEXT_PLAIN, "Media with id " + id +" unmarked as favorite Du hast die Funktion unmarkAsFavourite() im MediaController erreicht");
    }

    public Response rate(Request request) {


        //Dto erstellen
        RatingInput rating_dto = toObject(request.getBody(), RatingInput.class);

        //id setzen
        rating_dto.setMediaId(request.extractId());

        //DTO an Service weitergeben.
        this.mediaService.createRating(rating_dto);

        //just for now:
        return new Response(Status.CREATED, ContentType.TEXT_PLAIN, "Rating Submitted");
    }
}