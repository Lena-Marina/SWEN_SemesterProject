package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class UserController extends Controller {

/////// Diese zwei braucht es für die 1. Abgabe ///////
    public Response registerUser(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion registerUser() im UserController erreicht");
    }

    public Response loginUser(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion loginUser() im UserController erreicht");
    }
///////////////////////////////////////////////////////

    public Response getProfile(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getProfile() im UserController erreicht");
    }

    public Response updateProfile(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion updateProfile() im UserController erreicht");
    }

    public Response getRatings(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getRatings() im UserController erreicht");
    }

    public Response getFavourites(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getFavourites() im UserController erreicht");
    }

    public Response getRecommendations(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getRecommendations() im UserController erreicht");
    }
}
