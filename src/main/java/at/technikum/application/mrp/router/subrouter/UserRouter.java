package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.UserController;


public class UserRouter extends SubRouter<UserController> {

    public UserRouter() {
        this.controller = new UserController();
        register("/users/register", "POST", controller::registerUser); //überlegung ob nur /user in kombination mit "POST" nicht eh schon gut genug erklärt
        register("/users/login", "POST", controller::loginUser); //Überlegung durch /auth/token zu ersetzen
        register("/users/{userId}/profile", "GET", controller::getProfile);
        register("/users/{userId}/profile", "PUT", controller::updateProfile);
        register("/users/{userId}/ratings", "GET", controller::getRatings);
        register("/users/{userId}/favorites", "GET", controller::getFavourites);
        register("/users/{userId}/recommendations", "GET", controller::getRecommendations);
    }
}
