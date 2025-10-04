package at.technikum.application.mrp;

import at.technikum.application.common.Application;
import at.technikum.application.mrp.router.MainRouter;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

/*
    wird später die Datenbankverbindung bekommen
*/
public class MrpApplication implements Application {
    private final MainRouter mainRouter;

    public MrpApplication() {
        this.mainRouter = new MainRouter();
    }

    @Override
    public Response handle(Request request) {

        return mainRouter.route(request);

    }
}
