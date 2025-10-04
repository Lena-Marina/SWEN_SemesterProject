package at.technikum.application.common;

import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

/*
    Der Router findet heraus, welcher Controller für die Bearbeitung des Requests zuständig ist
    und übergibt diesem das Request-Objekt
*/
public interface Router {
    Response route(Request request);
}
