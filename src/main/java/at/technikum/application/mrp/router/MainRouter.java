package at.technikum.application.mrp.router;

import at.technikum.application.common.Router;
import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.ApplicationContext;
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

    public MainRouter(SubRouter[] routers) {
        register("/media",  routers[0]);
        register("/users",   routers[1]);
        register("/ratings",  routers[2]);
        register("/leaderboard",  routers[3]);
        register("/auth", routers[4]);
    }


    public void register(String path, SubRouter router) {
        routes.add(new Route<SubRouter>(path, router));
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
