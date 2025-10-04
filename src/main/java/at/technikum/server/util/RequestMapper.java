package at.technikum.server.util;

import at.technikum.server.http.Request;
import com.sun.net.httpserver.HttpExchange;

/*
    Übersetzt zwischen der HTTP-Library (hier: com.sun.net.httpserver.HttpExchange)
    und unserem internen Request-Objekt
*/
public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());
        //sein update holen

        return request;
    }
}
