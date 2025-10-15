package at.technikum.application.mrp.controller;

import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;


/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class LeaderboardController {

    public Response read(Request request)
    {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "read() erreicht");
    }

    /*Anmerkung von Lektor:
    Wir wollen in den Controllern nicht so
    eventuell an die Services fertige Objekte übergeben und nicht alle Parameter einzeln.
    in den Services die Namen eher grob halten.
    Services kann ich auch weiter differenzieren z.B. einen eigenen Auth-Service

    Eigentlich wollen wir in den Controllern eher Funktionen haben die mit CRUD übereinstimmen
    -> siehe To-Do-Controller in seiner Version.
    */
}
