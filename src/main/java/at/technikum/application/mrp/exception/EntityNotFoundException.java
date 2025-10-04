package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.NOT_FOUND) //content Type default: TEXT_PLAIN
public class EntityNotFoundException extends RuntimeException {

    //Viele verschiedene Konstruktoren - weil?
    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {

        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {

        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {

        super(cause);
    }

    public EntityNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
