package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class RatingController extends Controller {

    public Response likeRating(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion likeRating() im RatingController erreicht");
    }

    public Response updateRating(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion updateRating() im RatingController erreicht");
    }

    public Response deleteRating(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion deleteRating() im RatingController erreicht");
        //wenn Erfolg: Status: 204  Rating deleted
    }

    public Response confirmComment(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion confirmComment() im RatingController erreicht");
    }


}