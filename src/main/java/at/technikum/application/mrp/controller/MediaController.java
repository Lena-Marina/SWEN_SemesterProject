package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf
 nicht nur id aus dem Pfad, sondern auch Infos aus dem Body!*/
public class MediaController {

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
        //id und favourite extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion markAsFavourite() im MediaController erreicht");
    }

    public Response unmarkAsFavourite(Request request) {
        //id  extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion unmarkAsFavourite() im MediaController erreicht");
    }

    public Response rate(Request request) {
        //id extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion rateMedia() im MediaController erreicht");
    }
}