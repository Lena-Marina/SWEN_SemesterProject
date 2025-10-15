package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.service.AuthService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;



/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class UserController {

    //das später mit Dependency Injektion verbessern!
    private AuthService authService;
    private UserService userService;

    public UserController() {
        this.authService = new AuthService();
        this.userService = new UserService();
    }

    public Response read(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getProfile() im UserController erreicht");
    }

    public Response create(Request request) { //BRAUCHT ES FÜR ERSTE ABGABE
        //id -> wird von der Datenbank erstellt, geben wir daher nicht mit.

        //Username -> im Body als Json

        //Hashed Passwort -> im Body als Json allerdings nicht spezifiziert ob bereits gehashed

        //e-mail -> bekommen wir hier noch gar nicht, sondern erst bei Update

        //favorite Genre -> bekommen wir hier noch gar nicht, sondern erst bei Update
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion create() im UserController erreicht");
    }


    public Response update(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion update() im UserController erreicht");
    }


    public Response loginUser(Request request) { //BRAUCHT ES FÜR ERSTE ABGABE

        //Funktion des AuthService aufrufen.


        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion loginUser() im UserController erreicht");
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
