package at.technikum.application.common;

import at.technikum.application.mrp.exception.MethodNotAllowedException;
import at.technikum.application.mrp.exception.RouteNotFoundException;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import java.util.*;
import java.util.function.Function;

public abstract class SubRouter<T> implements Router {

    protected final List<Route <Function<Request, Response> >> routes = new ArrayList<>();
    protected T controller;


    protected void register(String pathPrefix, String method, Function<Request, Response> handler) {
        routes.add(new Route<>(method, pathPrefix, handler));
    }


    @Override
    public Response route(Request request) {
        /* Version mit Regex -> fürs nur Routen zu ressourcen-Intensiv
        for (Route<Function<Request, Response>> route : routes) {
            if (route.getPathAsPattern().matcher(request.getPath()).matches() //Pfade passen zusammen - werden als Regex-Patterns geprüft
                    && route.getMethod().equalsIgnoreCase(request.getMethod())) { //Methoden passen zusammen
                                        //Info: .equalsIgnoreCase(), vergleicht die Strings aber ohne auf Groß- und Kleinschreibung zu achten
                return route.getTarget().apply(request);
            }
        }
        */
        for(Route <Function<Request, Response> > route : routes) {
            if(request.getPath().contains(route.getPathPrefix())
                && request.getMethod().equals(route.getMethod()))
            {
                return route.getTarget().apply(request);
            }
        }


        //prüfen ob Pfad ohne Methode existiert:
        /*
        for (Route<Function<Request, Response>> route : routes) {
            boolean pathMatches = route.getPathAsPattern().matcher(request.getPath()).matches();
            boolean methodMatches = route.getMethod().equalsIgnoreCase(request.getMethod());

            if (pathMatches && !methodMatches) {
                throw new MethodNotAllowedException(request.getPath(), request.getMethod());
            }
        }

        */


        throw new RouteNotFoundException(request.getPath());

    }
}

