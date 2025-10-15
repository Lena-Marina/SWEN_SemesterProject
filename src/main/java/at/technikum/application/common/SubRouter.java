package at.technikum.application.common;

import at.technikum.application.mrp.exception.MethodNotAllowedException;
import at.technikum.application.mrp.exception.RouteNotFoundException;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import java.util.*;
import java.util.function.Function;

public abstract class SubRouter<T> implements Router {

    protected final List<Route <Function<Request, Response> >> routes = new ArrayList<>();
    protected T controller;


    protected void register(String pathPrefix, Method method, Function<Request, Response> handler) {
        routes.add(new Route<>(method, pathPrefix, handler));
    }


    @Override
    public Response route(Request request) {

        for(Route <Function<Request, Response> > route : routes) {
            if(request.getPath().contains(route.getPathPrefix())
            && request.getMethod() == route.getMethod())
            {
                return route.getTarget().apply(request);
            }
        }

        //Prüfen ob Pfad zwar existiert, aber nicht mit Methode
        for (Route<Function<Request, Response>> route : routes) {
            // Prüfen, ob der Pfad exakt oder per Präfix übereinstimmt
            if (request.getPath().contains(route.getPathPrefix())
            && request.getMethod() != route.getMethod()) {
                throw new MethodNotAllowedException(request.getPath(), request.getMethod().toString());
            }
        }


        throw new RouteNotFoundException(request.getPath());

    }
}

