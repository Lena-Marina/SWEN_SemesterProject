package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.RatingController;


public class RatingRouter extends SubRouter<RatingController> {

    public RatingRouter() {
        this.controller = new RatingController();

        register("/ratings/{ratingId}/like", "POST", controller::likeRating);
        register("/ratings/{ratingId}", "PUT", controller::updateRating);
        register("/ratings/{ratingId}", "DELETE", controller::deleteRating);
        register("/ratings/{ratingId}/confirm", "POST", controller::confirmComment);
    }

}
