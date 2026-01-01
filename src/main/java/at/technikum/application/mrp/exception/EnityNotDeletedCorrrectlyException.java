package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.INTERNAL_SERVER_ERROR)
public class EnityNotDeletedCorrrectlyException extends RuntimeException {
    public EnityNotDeletedCorrrectlyException(String message) {
        super(message);
    }
}
