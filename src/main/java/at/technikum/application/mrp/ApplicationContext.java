package at.technikum.application.mrp;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.*;
import at.technikum.application.mrp.repository.UserRepository;
import at.technikum.application.mrp.router.MainRouter;
import at.technikum.application.mrp.router.subrouter.*;
import at.technikum.application.mrp.router.util.TokenValidator;
import at.technikum.application.mrp.service.AuthService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.util.RequestMapper;

public class ApplicationContext {
    //Repositorys - Achtung in den Services muss ich immer auf die selbe Repository Instanz zugreifen,
    //solange sie als Datenbanken fungieren, sonst klappt es ja nicht mit dem Daten reinspeichern und wieder auslesen!
    UserRepository userRepository = new UserRepository();

    //Token Validator
    TokenValidator tokenValidator = new TokenValidator();

    //Subrouters
    SubRouter<?>[] routers = new SubRouter<?>[] {
            new MediaRouter(
                    new MediaController(),
                    tokenValidator
            ),

            new UserRouter(
                    new UserController(
                            new UserService(
                                    this.userRepository
                            )
                    ),
                    tokenValidator
            ),

            new RatingRouter(
                    new RatingController(),
                    tokenValidator
            ),

            new LeaderboardRouter(
                    new LeaderboardController(),
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


    //HTTP
    RequestMapper requestMapper = new RequestMapper();

}
