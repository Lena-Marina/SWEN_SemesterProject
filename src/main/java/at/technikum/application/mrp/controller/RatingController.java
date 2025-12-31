package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.service.RatingService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class RatingController extends Controller {
    private RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public Response update(Request request) //Update a Rating
    {
        //Dto erstellen
        RatingInput dto = toObject(request.getBody(), RatingInput.class);
        //id extrahieren und dto zufügen
        dto.setMediaId(request.extractIdAsString());

        //Aufruf Service Funktion
        this.ratingService.changeRating(dto);

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Rating zu Media mit id "+ dto.getMediaId() +" geupdated - Du hast die Funktion update() im RatingController erreicht");
    }

    public Response delete(Request request) { //deleteRating

        String ratingId = request.extractIdAsString();

        Rating deletedRating = this.ratingService.deleteRating(ratingId);

        //validation des deleted Rating

        //Response mit deleted Rating im Body erstellen - Response 204 ist definiert, dass er keinen Body mitschickt!
        return new Response(Status.UNMARKED,  ContentType.TEXT_PLAIN , "Rating mit id " + deletedRating.getId() + " gelöscht. - Du hast die Funktion delete() im RatingController erreicht");
        //wenn Erfolg: Status: 204  Rating deleted
    }

    public Response likeRating(Request request) {
        //just for now:
        String id = request.extractIdAsString();

        //später vermutlich geliktes Rating zurückgeben
        this.ratingService.likeRating(id);

        //gelikted Rating validieren.

        return new Response(Status.OK, ContentType.TEXT_PLAIN, "rating with id "+ id +" liked  - Du hast die Funktion likeRating() im RatingController erreicht");
    }


    //nicht CRUD-konforme Funktionen:
    public Response confirmComment(Request request) {

        String ratingId = request.extractIdAsString();

        //später: return Comment
        this.ratingService.confirmComment(ratingId);

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Comment confirmed - Du hast die Funktion confirmComment() im RatingController erreicht");
    }
}