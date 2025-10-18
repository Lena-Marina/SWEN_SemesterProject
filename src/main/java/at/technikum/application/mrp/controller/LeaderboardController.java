package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;


/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class LeaderboardController extends Controller {
    private UserService userService;

    public LeaderboardController(UserService userService) {
        this.userService = userService;
    }


    public Response read(Request request)
    {
        //ich habe nichts aus dem ich ein Objekt erstellen könnte -> bloß Service Funktion aufrufen
        List<User> mostAktive = userService.getMostAktive();

        //mostAktive wsl in ein Json umwandeln?

        //just for now:
        return listToJson(mostAktive, Status.OK);
    }

}
