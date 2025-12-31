package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.INTERNAL_SERVER_ERROR)
public class InvalidEntityException extends RuntimeException {
    public InvalidEntityException(String message) {
        super(message);
    }
}
