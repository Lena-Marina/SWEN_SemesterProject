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
        register("/users/register", false, Method.POST, controller::create); //doing
        //register("/users/login", Method.POST, controller::loginUser); //Überlegung durch /auth/token zu ersetzen -> wurde gemacht
        register("/profile", true, Method.GET, controller::read);
        register("/profile", true, Method.PUT, controller::update);
        register("/ratings", true, Method.GET, controller::getRatings);
        register("/favorites", true, Method.GET, controller::getFavourites);
        register("/recommendations",true,  Method.GET, controller::getRecommendations);
    }

}
