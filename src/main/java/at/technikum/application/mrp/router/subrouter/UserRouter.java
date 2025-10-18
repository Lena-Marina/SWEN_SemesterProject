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
        register("/users/register", false, Method.POST, controller::create); //check //überlegung ob nur /user in kombination mit "POST" nicht eh schon gut genug erklärt
        //register("/users/login", Method.POST, controller::loginUser); //Überlegung durch /auth/token zu ersetzen -> wurde gemacht
        register("/profile", true, Method.GET, controller::read); //read a User
        register("/profile", true, Method.PUT, controller::update); //Update a User
        register("/ratings", true, Method.GET, controller::getRatings);
        register("/favorites", true, Method.GET, controller::getFavourites);
        register("/recommendations",true,  Method.GET, controller::getRecommendations);
    }

}
