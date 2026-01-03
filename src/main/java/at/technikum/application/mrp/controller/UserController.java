package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.exception.NotJsonBodyException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.RecommendationRequest;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.model.dto.UserUpdate;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class UserController extends Controller {

    private UserService userService;
    private MediaService mediaService;

    public UserController(UserService userService,  MediaService mediaService) {
        this.userService = userService;
        this.mediaService = mediaService;
    }

    public Response read(Request request) {
        String userID = request.extractIdAsString();

        User user = this.userService.getUserByID(userID);

        return json(user, Status.OK);
    }

    public Response create(Request request) {
        try {
            // Request-Body → DTO konvertieren
            UserCredentials userCredentials = toObject(request.getBody(), UserCredentials.class);

            //DEBUGGING
            System.out.println("---------------------------------");
            System.out.println("DEBUG in UserController::create() ");
            System.out.println("DEBUG: username = " + userCredentials.getUsername());
            System.out.println("DEBUG: password = " + userCredentials.getPassword());

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

        //DEBUGGING
        System.out.println("---------------------------------");
        System.out.println("DEBUG | RAW BODY: " + request.getBody());
        System.out.println("DEBUG | ID: " + request.extractIdAsString());

        // Request-Body → DTO konvertieren
        UserUpdate update = toObject(request.getBody(), UserUpdate.class); //hier drinnen wirft es fehler... aber warum?

        update.setUserID(request.extractIdAsUUID());

        User updatedUser = this.userService.updateUser(update);

        return  json(updatedUser, Status.OK);
    }


    public Response getRatings(Request request) {

        UUID userID = request.extractIdAsUUID();

        List<Rating> userRatings = this.userService.getUserRatings(userID);

        return json(userRatings, Status.OK);
    }

    public Response getFavourites(Request request) {
        UUID userId = request.extractIdAsUUID();

        List<Media> favouriteMedias = this.userService.getUsersFavourites(userId);

        return json(favouriteMedias, Status.OK);
    }

    public Response getRecommendations(Request request) {

        //User ID aus Pfad filtern
        UUID userID = request.extractIdAsUUID();

        //Filter aus Query-Param: "type" auslesen |  Möglichkeiten: genre und content - schauen ob type mitgegeben wurde.
        String type = request.getQueryParams().get("type");

        //DTO aus den Infos machen.
        RecommendationRequest dto = new RecommendationRequest(userID, type);

        //Service Funktion aufrufen.
        List<Media> recommendations = this.mediaService.getRecommendation(dto);

        return listToJson(recommendations, Status.OK);
    }


}
