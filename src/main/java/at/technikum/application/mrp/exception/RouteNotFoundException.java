package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.NOT_FOUND)
public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String path) {
        super("No route for " + path);
    }

}
