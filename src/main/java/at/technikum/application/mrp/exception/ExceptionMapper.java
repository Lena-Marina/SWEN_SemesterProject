package at.technikum.application.mrp.exception;

import at.technikum.server.http.ContentType;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

/*
Lösung mit Anotationen gewählt, um besser auf die spätere Arbeit mit SpringBoot und anderen Frameworks
vorbereitet zu sein.

Die funktion toResponse wird momentan (09.10.2025) im (package at.technikum.server.)handler aufgerufen
*/

public class ExceptionMapper {


    public ExceptionMapper() {

    }

    static public Response toResponse(Exception exception) { //static damit ich es im Handler (package at.technikum.server.handler) aufrufen kann - wsl später anders schöner? also irgendwo einen ExceptionMapper instanzieren

        Class<?> clazz = exception.getClass();

        HttpError annotation = clazz.getAnnotation(HttpError.class);
        if (annotation != null) {
            Response response = new Response();

            response.setStatus(annotation.status()); //wenn das nicht gesetzt wäre, wäre annotation == null und wir wären gar nicht hier
            response.setContentType(annotation.contentType()); //hat einen default, kann also nicht null sein

            response.setBody(exception.getMessage());
            String body = exception.getMessage();
            if (body == null || body.isBlank()) {
                body = "No details provided";
            }

            return response;
        }

        Response defaultResponse = new Response();
        defaultResponse.setStatus(Status.INTERNAL_SERVER_ERROR);
        defaultResponse.setContentType(ContentType.TEXT_PLAIN);
        defaultResponse.setBody(exception.getMessage());
        return defaultResponse;
    }
}