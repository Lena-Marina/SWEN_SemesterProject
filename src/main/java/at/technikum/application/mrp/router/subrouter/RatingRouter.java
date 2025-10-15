package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.RatingController;
import at.technikum.server.http.Method;


public class RatingRouter extends SubRouter<RatingController> {

    public RatingRouter() {
        this.controller = new RatingController();

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/confirm", Method.POST, controller::confirmComment);
        register("/like", Method.POST, controller::likeRating);

        register("/ratings/", Method.PUT, controller::update);
        register("/ratings/", Method.DELETE, controller::delete);

    }

}
