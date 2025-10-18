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

    //Allgemeine Klassen
    TokenValidator tokenValidator = new TokenValidator();
    RequestMapper requestMapper = new RequestMapper();


    //Subrouters
    SubRouter<?>[] routers = new SubRouter<?>[] {
            new MediaRouter(
                    new MediaController(
                            mediaService
                    ),
                    tokenValidator
            ),

            new UserRouter(
                    new UserController(
                            userService,
                            mediaService
                    ),
                    tokenValidator
            ),

            new RatingRouter(
                    new RatingController(
                            ratingService
                    ),
                    tokenValidator
            ),

            new LeaderboardRouter(
                    new LeaderboardController(
                            userService
                    ),
                    tokenValidator
            ),

            new AuthRouter(
                    new AuthController(
                            new AuthService(
                                    this.userRepository)
                    ),
                    tokenValidator
            )
    };

    //Router
    MainRouter mainRouter = new MainRouter(routers); //media - user - rating -leaderboard

    public MainRouter getMainRouter() {
        return mainRouter;
    }

}
