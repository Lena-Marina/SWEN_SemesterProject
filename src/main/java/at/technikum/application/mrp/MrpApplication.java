package at.technikum.application.mrp;

import at.technikum.application.common.Application;
import at.technikum.application.mrp.router.MainRouter;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

/*
    wird später die Datenbankverbindung bekommen

    Sie darf (in unserem Projekt) die anderen Klassen wie den Exception Mapper verwalten.
    Wenn das von einer anderen Klasse übernommen wird, wird diese von einer Klasse die häufig
    z.B. AppConfig oder Injektor oder ApplicationContext(z.B. in Springboot) genannt wird
*/
public class MrpApplication implements Application {
    private final ApplicationContext context;

    public MrpApplication() {
        this.context = new ApplicationContext();
    }

    @Override
    public Response handle(Request request) {

        return context.getMainRouter().route(request);

    }
}
