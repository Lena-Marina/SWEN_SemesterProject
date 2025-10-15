package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.UserController;
import at.technikum.server.http.Method;


public class UserRouter extends SubRouter<UserController> {

    public UserRouter() {
        this.controller = new UserController();

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/users/register", Method.POST, controller::create); //überlegung ob nur /user in kombination mit "POST" nicht eh schon gut genug erklärt
        register("/users/login", Method.POST, controller::loginUser); //Überlegung durch /auth/token zu ersetzen
        register("/profile", Method.GET, controller::read); //read a User
        register("/profile", Method.PUT, controller::update); //Update a User
        register("/ratings", Method.GET, controller::getRatings);
        register("/favorites", Method.GET, controller::getFavourites);
        register("/recommendations", Method.GET, controller::getRecommendations);
    }

}
