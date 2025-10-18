package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.exception.NotJsonBodyException;
import at.technikum.application.mrp.model.Token;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.service.AuthService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class AuthController extends Controller {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Response getToken (Request request)
    {
        try {
            // Request-Body → DTO konvertieren
            UserCredentials userCredentials = toObject(request.getBody(), UserCredentials.class);

            // DTO an Service weitergeben und User speichern
            Token token = authService.getToken(userCredentials);

            // JSON-Response zurückgeben mit Status 200 Ok
            return json(token, Status.OK);
        } catch (Exception e) {
            // JSON-Konvertierungsfehler oder Service-Fehler abfangen
            throw new NotJsonBodyException(e.getMessage());
        }

    }

}
