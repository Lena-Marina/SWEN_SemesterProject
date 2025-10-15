package at.technikum.application.mrp.router.subrouter;


import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.MediaController;
import at.technikum.server.http.Method;



public class MediaRouter extends SubRouter<MediaController> {

    public MediaRouter(MediaController mediaController) {
        this.controller = mediaController;

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/favorite", Method.POST, controller::markAsFavourite);
        register("/favorite", Method.DELETE, controller::unmarkAsFavourite);
        register("/rate", Method.POST, controller::rate);

        register("/media/", Method.DELETE, controller::delete);
        register("/media/", Method.PUT, controller::update);
        register("/media/", Method.GET, controller::read);

        register("/media", Method.GET, controller::readAll);
        register("/media", Method.POST, controller::create);

    }

}
