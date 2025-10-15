package at.technikum.application.mrp.router.subrouter;


import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.MediaController;



public class MediaRouter extends SubRouter<MediaController> {

    public MediaRouter() {
        this.controller = new MediaController();

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/favorite", "POST", controller::markAsFavourite);
        register("/favorite", "DELETE", controller::unmarkAsFavourite);
        register("/rate", "POST", controller::rate);

        register("/media/", "DELETE", controller::delete);
        register("/media/", "PUT", controller::update);
        register("/media/", "GET", controller::read);

        register("/media", "GET", controller::readAll);
        register("/media", "POST", controller::create);

    }

}
