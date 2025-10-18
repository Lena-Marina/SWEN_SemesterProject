package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.AuthController;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.server.http.Method;


public class AuthRouter extends SubRouter<AuthController> {
    public AuthRouter(AuthController authController, TokenValidator tokenValidator) {
        super(authController, tokenValidator);

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/token", false, Method.GET, controller::getToken); //check
    }
}
