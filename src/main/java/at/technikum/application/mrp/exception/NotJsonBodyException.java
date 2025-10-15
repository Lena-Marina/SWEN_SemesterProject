package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.BAD_REQUEST)
public class NotJsonBodyException extends RuntimeException {
    public NotJsonBodyException(String message) {
        super(message);
    }
}
