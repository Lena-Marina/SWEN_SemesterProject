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
        register("/favorite", true, Method.DELETE, controller::unmarkAsFavourite); //done
        register("/rate", true, Method.POST, controller::rate); //done

        register("/media/", true, Method.DELETE, controller::delete); //done
        register("/media/", true, Method.PUT, controller::update); //done
        register("/media/",true,  Method.GET, controller::read); /* 4.) Media Entry Details -> Ratings mitgeben (aber kommentar nur, wenn confirmed), average score berechnen*/

        register("/media",true,  Method.GET, controller::readAll); /* 3.) Alle Einträge sortiert und nach Auswahlkriterium*/
        register("/media",true,  Method.POST, controller::create); //done

    }

}
