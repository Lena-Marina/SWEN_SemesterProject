package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf
* nicht nur id aus dem Pfad, sondenr auch Infos aus dem Body!*/
public class MediaController extends Controller {


    public Response getAllMedia(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion getAllMedia() im MediaController erreicht");

    }

    public Response createMedia(Request request) {
        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion createMedia() im MediaController erreicht");
    }

    public Response deleteMedia(Request request) {
        //id extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion deleteMedia() im MediaController erreicht");
    }

    public Response updateMedia(Request request) {
        // id extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion updateMedia() im MediaController erreicht");
    }

    public Response getMedia(Request request) {
        String mediaID = extractID(request);

        if (mediaID != null) {
            //Funktion des Service aufrufen
            return new Response(Status.OK, ContentType.TEXT_PLAIN, "funktion getMedia() im MediaController mit der mediaId: " + mediaID + " aufgerufen");
        }

        //bis bessere Lösung gefunden
        return new Response(Status.BAD_REQUEST, ContentType.TEXT_PLAIN, "Ungueltiger Pfad: " + request.getPath());
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

    public Response rateMedia(Request request) {
        //id extrahieren

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Du hast die Funktion rateMedia() im MediaController erreicht");
    }



}