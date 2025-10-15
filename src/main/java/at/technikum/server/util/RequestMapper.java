package at.technikum.server.util;

import at.technikum.server.http.Request;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import at.technikum.server.http.Method;

/*
    Übersetzt zwischen der HTTP-Library (hier: com.sun.net.httpserver.HttpExchange)
    und unserem internen Request-Objekt
*/
public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) {
        Request request = new Request();
        request.setMethod(Method.valueOf(exchange.getRequestMethod()));
        request.setPath(exchange.getRequestURI().getPath());

        InputStream is = exchange.getRequestBody();

        if (is == null) {
            return request;
        }

        try {
            byte[] buf = is.readAllBytes();
            request.setBody(new String(buf, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            request.setBody("");
        }

        return request;
    }
}
