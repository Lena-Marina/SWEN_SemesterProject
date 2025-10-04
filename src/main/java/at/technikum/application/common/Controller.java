package at.technikum.application.common;

import at.technikum.server.http.Request;

/*
    "Schnittstelle" zwischen Router und Services
    übersetzt zwischen HTTP und Businesslogik
    ruft im Endeffekt die zur Http-Anfrage passende Funktion auf,
    da diese aber kein Http-Request Erwarten, sondern einfachere Parameter,
    extrahiert der Controller diese Parameter aus dem Request-Objekt.
 */
public abstract class Controller {

    //HILFSFUNKTIONEN
    public String extractID(Request request) {

        String path = request.getPath();

        String[] segments = path.split("/");

        if (segments.length >= 3) {
            return segments[2]; // 0 = "", 1 = "media" oder "user" oder "ratings", 2 = z.B.: "1234"
        }

        return null;
    }
}
