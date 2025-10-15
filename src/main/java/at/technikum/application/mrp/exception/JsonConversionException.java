package at.technikum.application.mrp.exception;

import at.technikum.server.http.Status;

@HttpError(status = Status.BAD_REQUEST)
public class JsonConversionException extends RuntimeException {
    public JsonConversionException(String message) {
        super(message);
    }
}
