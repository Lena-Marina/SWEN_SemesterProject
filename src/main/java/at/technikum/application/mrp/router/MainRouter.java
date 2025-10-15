package at.technikum.application.mrp.router;

import at.technikum.application.common.Router;
import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.exception.RouteNotFoundException;
import at.technikum.application.mrp.router.subrouter.LeaderboardRouter;
import at.technikum.application.mrp.router.subrouter.MediaRouter;
import at.technikum.application.mrp.router.subrouter.RatingRouter;
import at.technikum.application.mrp.router.subrouter.UserRouter;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.application.common.Route;

import java.util.ArrayList;
import java.util.List;

public class MainRouter implements Router {
    /*
    S - one Reason to change: New Router exists or old one is deleted
    O
    L
    I
    D
    */

    private final List<Route <SubRouter> > routes = new ArrayList<>();

    public MainRouter() {
        register("/media", new MediaRouter());
        register("/users", new UserRouter());
        register("/ratings", new RatingRouter());
        register("/leaderboard", new LeaderboardRouter());
    }

    public void register(String pathPrefix, SubRouter router) {
        routes.add(new Route<SubRouter>(pathPrefix, router));
    }

    @Override
    public Response route(Request request) {

        for (Route<SubRouter> route : routes) {
            if (request.getPath().startsWith(route.getPathPrefix())) {
                return route.getTarget().route(request);
            }
        }

        throw new RouteNotFoundException(request.getPath());
    }
}
