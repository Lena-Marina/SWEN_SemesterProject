package at.technikum.server.util;

import at.technikum.server.http.Request;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

        //Header (im Moment nur Authenticate) auslesen
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        request.setAuthorizationHeader(authHeader);

        //body auslesen
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

        //Query-Parameter auslesen
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            Map<String, String> queryParams = new HashMap<>();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    queryParams.put(pair[0], pair[1]);
                } else {
                    queryParams.put(pair[0], "");
                }
            }
            request.setQueryParams(queryParams);
        }

        return request;
    }
}
