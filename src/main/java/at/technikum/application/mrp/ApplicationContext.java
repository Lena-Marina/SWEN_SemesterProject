package at.technikum.application.mrp;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.*;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.UserRepository;
import at.technikum.application.mrp.router.MainRouter;
import at.technikum.application.mrp.router.subrouter.*;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.application.mrp.service.AuthService;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.application.mrp.service.RatingService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.util.RequestMapper;

public class ApplicationContext {
    //Repositorys - Achtung in den Services muss ich immer auf die selbe Repository Instanz zugreifen,
    //solange sie als Datenbanken fungieren, sonst klappt es ja nicht mit dem Daten reinspeichern und wieder auslesen!
    UserRepository userRepository = new UserRepository();
    MediaRepository mediaRepository = new MediaRepository();

    //Services
    UserService userService = new UserService(userRepository);
    MediaService mediaService = new MediaService(mediaRepository);
    RatingService ratingService = new RatingService(mediaRepository);
    AuthService authService = new AuthService(userRepository);

    //Controller
    MediaController mediaController = new MediaController(mediaService);
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
