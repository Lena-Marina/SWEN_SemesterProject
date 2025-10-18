package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
