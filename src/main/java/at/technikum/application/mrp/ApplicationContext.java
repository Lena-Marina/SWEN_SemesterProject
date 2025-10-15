package at.technikum.application.mrp;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.LeaderboardController;
import at.technikum.application.mrp.controller.MediaController;
import at.technikum.application.mrp.controller.RatingController;
import at.technikum.application.mrp.controller.UserController;
import at.technikum.application.mrp.repository.UserRepository;
import at.technikum.application.mrp.router.MainRouter;
import at.technikum.application.mrp.router.subrouter.LeaderboardRouter;
import at.technikum.application.mrp.router.subrouter.MediaRouter;
import at.technikum.application.mrp.router.subrouter.RatingRouter;
import at.technikum.application.mrp.router.subrouter.UserRouter;
import at.technikum.application.mrp.service.AuthService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.util.RequestMapper;

public class ApplicationContext {

    //Subrouters
    SubRouter<?>[] routers = new SubRouter<?>[] {
            new MediaRouter(
                    new MediaController()),

            new UserRouter(
                    new UserController(
                            new UserService(
                                    new UserRepository()),
                                    new AuthService(
                                            new UserRepository()))),

            new RatingRouter(
                    new RatingController()),

            new LeaderboardRouter(
                    new LeaderboardController())
    };

    //Router
    MainRouter mainRouter = new MainRouter(routers); //media - user - rating -leaderboard

    public MainRouter getMainRouter() {
        return mainRouter;
    }



    //HTTP
    RequestMapper requestMapper = new RequestMapper();

}
