package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.INTERNAL_SERVER_ERROR)
public class EntityNotSavedCorrectlyException extends RuntimeException {
    public EntityNotSavedCorrectlyException(String message) {
        super(message);
    }
}
