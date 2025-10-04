package at.technikum.application.mrp.router.subrouter;


import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.MediaController;



public class MediaRouter extends SubRouter<MediaController> {

    public MediaRouter() {
        this.controller = new MediaController();

        register("/media", "GET", controller::getAllMedia);
        register("/media", "POST", controller::createMedia);
        register("/media/{MediaId}", "DELETE", controller::deleteMedia);
        register("/media/{mediaId}", "PUT", controller::updateMedia);
        register("/media/{mediaId}", "GET", controller::getMedia);
        register("/media/{mediaId}/favorite", "POST", controller::markAsFavourite);
        register("/media/{mediaId}/favorite", "DELETE", controller::unmarkAsFavourite);
        register("/media/{mediaId}/rate", "POST", controller::rateMedia);
    }

}
