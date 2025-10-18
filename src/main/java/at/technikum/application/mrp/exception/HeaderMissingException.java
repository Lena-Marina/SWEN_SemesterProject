package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.UNAUTHORIZED)
public class HeaderMissingException extends RuntimeException {
    public HeaderMissingException(String message) {
        super(message);
    }
}
