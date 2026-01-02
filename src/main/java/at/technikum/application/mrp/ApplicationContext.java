package at.technikum.application.mrp;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.*;
import at.technikum.application.mrp.repository.*;
import at.technikum.application.mrp.router.MainRouter;
import at.technikum.application.mrp.router.subrouter.*;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.application.mrp.service.AuthService;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.application.mrp.service.RatingService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.util.RequestMapper;

public class ApplicationContext {
    //ConnectionPool -> alle Repos bekommen Zugriff auf den ConnectionPool
    private final ConnectionPool connectionPool = new ConnectionPool(
            "postgresql",
            "localhost",
            5432,
            "swen1user",
            "swen1db", // secretManager.get("DB_PW")
            "mrpdb");

    //Repositorys erhalten den ConnectionPool
    UserRepository userRepository = new UserRepository(connectionPool);
    MediaRepository mediaRepository = new MediaRepository(connectionPool);
    FavoriteRepository favoriteRepository = new FavoriteRepository(connectionPool);
    RatingRepository ratingRepository = new RatingRepository(connectionPool);

    //Services
    UserService userService = new UserService(userRepository, ratingRepository);
    MediaService mediaService = new MediaService(mediaRepository, userRepository, favoriteRepository);
    RatingService ratingService = new RatingService(ratingRepository, mediaRepository, userRepository);
    AuthService authService = new AuthService(userRepository);

    //Controller
    MediaController mediaController = new MediaController(mediaService, userService, ratingService);
    UserController userController = new UserController(userService, mediaService);
    RatingController ratingController = new RatingController(ratingService);
    LeaderboardController leaderboardController = new LeaderboardController(userService);
    AuthController authController = new AuthController(authService);

    //Allgemeine Klassen
    TokenValidator tokenValidator = new TokenValidator();
    RequestMapper requestMapper = new RequestMapper();


    //Subrouters
    SubRouter<?>[] routers = new SubRouter<?>[] {
            new MediaRouter(mediaController, tokenValidator),
            new UserRouter(userController, tokenValidator),
            new RatingRouter(ratingController, tokenValidator),
            new LeaderboardRouter(leaderboardController, tokenValidator),
            new AuthRouter(authController, tokenValidator)
    };

    //Main-Router
    MainRouter mainRouter = new MainRouter(routers); //[0]media - [1]user - [2]rating -[3]leaderboard - [4]auth



    public MainRouter getMainRouter() {
        return mainRouter;
    }

}
