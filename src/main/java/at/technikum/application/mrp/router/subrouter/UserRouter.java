package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.UserController;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.server.http.Method;


public class UserRouter extends SubRouter<UserController> {

    public UserRouter(UserController userController, TokenValidator tokenValidator) {
        super(userController, tokenValidator);

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/users/register", false, Method.POST, controller::create); //Done
        //register("/users/login", Method.POST, controller::loginUser); //wurde durch /auth/token ersetzt
        register("/profile", true, Method.GET, controller::read); //Done
        register("/profile", true, Method.PUT, controller::update); //Done
        register("/ratings", true, Method.GET, controller::getRatings); // Done
        register("/favorites", true, Method.GET, controller::getFavourites); // Done?
        register("/recommendations",true,  Method.GET, controller::getRecommendations);  // 2.) receives recommendations based on previous rating behavior and content similarity // -> Vorschläge von Genre die gut bewertet wurden
    }

}
