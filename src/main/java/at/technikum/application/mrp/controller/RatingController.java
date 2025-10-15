package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class RatingController {

    public Response update(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion update() im RatingController erreicht");
    }

    public Response delete(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion delete() im RatingController erreicht");
        //wenn Erfolg: Status: 204  Rating deleted
    }

    public Response likeRating(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion likeRating() im RatingController erreicht");
    }


    //nicht CRUD-konforme Funktionen:
    public Response confirmComment(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion confirmComment() im RatingController erreicht");
    }
}