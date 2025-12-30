package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.CONFLICT)
public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
