package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.exception.NotJsonBodyException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.RecommendationRequest;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;


/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class UserController extends Controller {

    //das später mit Dependency Injektion verbessern!
    private UserService userService;
    private MediaService mediaService;

    public UserController(UserService userService,  MediaService mediaService) {

        this.userService = userService;
        this.mediaService = mediaService;
    }

    public Response read(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getProfile() im UserController erreicht");
    }

    public Response create(Request request) { //BRAUCHT ES FÜR ERSTE ABGABE
        try {
            // Request-Body → DTO konvertieren
            UserCredentials userCredentials = toObject(request.getBody(), UserCredentials.class);

            // DTO an Service weitergeben und User speichern
            User savedUser = userService.registerUser(userCredentials);

            // JSON-Response zurückgeben mit Status 201 Created
            return json(savedUser, Status.CREATED);
        } catch (Exception e) {
            // JSON-Konvertierungsfehler oder Service-Fehler abfangen
            throw new NotJsonBodyException(e.getMessage());
        }
    }


    public Response update(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion update() im UserController erreicht");
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

        //User ID aus Pfad filtern
        String id = request.extractId(); //throwed eine Exception wenn es nicht klappt

        //querys filtern | möglichkeiten: genre und content - schauen ob type mitgegeben wurde.

        String type = null;
        if (request.getQueryParams() != null) {
            type = request.getQueryParams().get("type");
        } // Da die query nicht required ist, in den spezifikationen, muss es auch möglich sein, dass es null ist.
        //Daher keine Exception if == null

        //DTO aus den Infos machen.
        RecommendationRequest dto = new RecommendationRequest(id, type);

        //Service Funktion aufrufen.
        List<Media> recommendations = this.mediaService.getRecommendation(dto);


        //just for now:
        return listToJson(recommendations, Status.OK);
    }


}
