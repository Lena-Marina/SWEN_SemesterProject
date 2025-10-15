package at.technikum.application.common;

import at.technikum.application.mrp.exception.JsonConversionException;
import at.technikum.application.mrp.exception.NotJsonBodyException;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
    "Schnittstelle" zwischen Router und Services
    übersetzt zwischen HTTP und Businesslogik
    ruft im Endeffekt die zur Http-Anfrage passende Funktion auf,
    da diese aber kein Http-Request Erwarten, sondern einfachere Parameter,
    extrahiert der Controller diese Parameter aus dem Request-Objekt.
 */

public interface Controller {

    //BasisFunktionen eines Controllers
    public Response handle(Request request);

    public Response readAll(Request request);

    public Response read( Request request);

    public Response create(Request request);

    public Response update(Request request);

    public Response delete(Request request);


    private <T> T toObject(String content, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception ex) {
            throw new NotJsonBodyException(ex.getMessage());
        }
    }

    private Response json(Object o, Status status) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(o);
            return r(status, ContentType.APPLICATION_JSON, json);
        } catch (Exception ex) {
            throw new JsonConversionException(ex.getMessage());
        }
    }

    private Response r(Status status, ContentType contentType, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setContentType(contentType);
        response.setBody(body);

        return response;
    }

    //Zusätzliche
    private String extractID(Request request) {

        String path = request.getPath();

        String[] segments = path.split("/");

        if (segments.length >= 3) {
            return segments[2]; // 0 = "", 1 = "media" oder "user" oder "ratings", 2 = z.B.: "1234"
        }

        return null;
    }


}



