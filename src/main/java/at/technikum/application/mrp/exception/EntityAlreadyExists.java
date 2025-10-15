package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.CONFLICT)
public class EntityAlreadyExists extends RuntimeException {
    public EntityAlreadyExists(String message) {
        super(message);
    }
}
