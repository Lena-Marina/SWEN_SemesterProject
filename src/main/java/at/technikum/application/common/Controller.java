package at.technikum.application.common;

import at.technikum.application.mrp.exception.JsonConversionException;
import at.technikum.application.mrp.exception.NotJsonBodyException;
import at.technikum.application.mrp.model.User;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/*
    "Schnittstelle" zwischen Router und Services
    übersetzt zwischen HTTP und Businesslogik
    ruft im Endeffekt die zur Http-Anfrage passende Funktion auf,
    da diese aber kein Http-Request Erwarten, sondern einfachere Parameter,
    extrahiert der Controller diese Parameter aus dem Request-Objekt.
 */

public abstract class Controller {

    protected <T> T toObject(String content, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception ex) {
            throw new NotJsonBodyException("der Body enthält kein JSON!" + ex.getMessage());
        }
    }

    protected Response ok() {
        return status(Status.OK);
    }

    protected Response status(Status status) {
        return text(status.getMessage(), status);
    }

    protected Response text(String text) {
        return text(text, Status.OK);
    }

    protected Response text(String text, Status status) {
        return response(status, ContentType.TEXT_PLAIN, text);
    }

    protected Response json(Object o, Status status) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonBody = objectMapper.writeValueAsString(o);
            return response(status, ContentType.APPLICATION_JSON, jsonBody);
        } catch (Exception ex) {
            throw new JsonConversionException("Ich kann aus diesem Objekt kein JSON machen!"/*ex.getMessage()*/);
        }
    }

    private Response response(Status status, ContentType contentType, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setContentType(contentType);
        response.setBody(body);

        return response;
    }

    protected <T> Response listToJson(List<T> list, Status status) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonBody = objectMapper.writeValueAsString(list);
            return response(status, ContentType.APPLICATION_JSON, jsonBody);
        } catch (Exception ex) {
            throw new JsonConversionException(ex.getMessage());
        }
    }
}




