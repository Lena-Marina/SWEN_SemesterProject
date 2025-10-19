package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.RatingController;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.server.http.Method;


public class RatingRouter extends SubRouter<RatingController> {

    public RatingRouter(RatingController ratingController, TokenValidator tokenValidator) {
        super(ratingController, tokenValidator);

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/confirm", true, Method.POST, controller::confirmComment); //check
        register("/like", true, Method.POST, controller::likeRating); //check

        register("/ratings/", true, Method.PUT, controller::update); //check
        register("/ratings/", true,  Method.DELETE, controller::delete); //check

    }

}
