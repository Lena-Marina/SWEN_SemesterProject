package at.technikum.application.mrp.router.subrouter;

import at.technikum.application.common.SubRouter;
import at.technikum.application.mrp.controller.LeaderboardController;

public class LeaderboardRouter extends SubRouter<LeaderboardController> {

    public LeaderboardRouter() {
        this.controller = new LeaderboardController(); //nein ich erstelle hier nicht den Controller,
        //sondern die Router bekommen ihre Controller aus dem Context (dependency Injektion)
        //damit der Router nicht verantworlich ist für die Verwaltung des Controllers

        //ACHTUNG: die Reihenfolge der Registrierungen ist wichtig,
        //je allgemeiner ein Pfad-Abschnitt ist, desto später muss er registriert werden!
        register("/leaderboard", "GET", controller::read);
    }
}
