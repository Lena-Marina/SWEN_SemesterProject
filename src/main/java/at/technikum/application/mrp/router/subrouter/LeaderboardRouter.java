package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.LeaderboardController;
import at.technikum.server.http.Method;

public class LeaderboardRouter extends SubRouter<LeaderboardController> {

    public LeaderboardRouter(LeaderboardController leaderboardController) {
        this.controller = leaderboardController;

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/leaderboard", Method.GET, controller::read);
    }
}
