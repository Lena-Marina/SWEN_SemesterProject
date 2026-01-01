package at.technikum.application.mrp.router.subrouter;


import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.MediaController;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.server.http.Method;



public class MediaRouter extends SubRouter<MediaController> {

    public MediaRouter(MediaController mediaController, TokenValidator tokenValidator) {
        super(mediaController, tokenValidator);

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/favorite", true, Method.POST, controller::markAsFavourite); //done
        register("/favorite", true, Method.DELETE, controller::unmarkAsFavourite);
        register("/rate", true, Method.POST, controller::rate);

        register("/media/", true, Method.DELETE, controller::delete); //done
        register("/media/", true, Method.PUT, controller::update); //done
        register("/media/",true,  Method.GET, controller::read);

        register("/media",true,  Method.GET, controller::readAll);
        register("/media",true,  Method.POST, controller::create); //done

    }

}
