package at.technikum.application.common;

import at.technikum.application.mrp.exception.InvalidTokenException;
import at.technikum.application.mrp.exception.MethodNotAllowedException;
import at.technikum.application.mrp.exception.RouteNotFoundException;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import java.util.*;
import java.util.function.Function;

public abstract class SubRouter<T> implements Router {

    protected final List<Route <Function<Request, Response> >> routes = new ArrayList<>();
    protected T controller;
    protected TokenValidator validator;

    public SubRouter(T controller, TokenValidator validator) {
        this.controller = controller;
        this.validator = validator;
    }

    protected void register(String pathPrefix, boolean isProtected, Method method, Function<Request, Response> handler) {
        routes.add(new Route<>(method, pathPrefix, handler, isProtected));
    }


    @Override
    public Response route(Request request) {

        //Prüfen ob Pfad + Methode existiert
        for(Route <Function<Request, Response> > route : routes) {
            if(request.getPath().contains(route.getPathPrefix())
            && request.getMethod() == route.getMethod())
            {
                //Prüfen ob Pfad geschützt, wenn ja nur wenn Token ein echter sein kann, weiter machen.
                if(route.isProtected())
                {
                    if(!validator.isValidToken(request.getAuthorizationHeader()))
                    {
                        throw new InvalidTokenException("the Token is not valid!");
                    }
                }

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

