package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
