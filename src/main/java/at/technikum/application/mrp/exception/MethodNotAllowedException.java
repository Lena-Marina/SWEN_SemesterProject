package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.METHOD_NOT_ALLOWED)
public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException(String path, String method) {
        super("Pfad " + path + " existiert, aber Methode " + method + " nicht erlaubt");
    }

}
