package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.RatingController;


public class RatingRouter extends SubRouter<RatingController> {

    public RatingRouter() {
        this.controller = new RatingController();

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/confirm", "POST", controller::confirmComment);
        register("/like", "POST", controller::likeRating);

        register("/ratings/", "PUT", controller::update);
        register("/ratings/", "DELETE", controller::delete);

    }

}
