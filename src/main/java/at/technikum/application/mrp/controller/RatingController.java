package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.CommentConfirm;
import at.technikum.application.mrp.model.dto.LikedBy;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.service.RatingService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.UUID;

/*in den Controllern extrahiere ich die Parameter und rufe die Service Funktionen auf*/
public class RatingController extends Controller {
    private RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public Response update(Request request) //Update a Rating
    {
        //Dto erstellen
        RatingInput dto = toObject(request.getBody(), RatingInput.class);
        //id extrahieren und dto zufügen
        dto.setMediaId(request.extractIdAsUUID());

        //Aufruf Service Funktion
        this.ratingService.changeRating(dto);

        //just for now:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Rating zu Media mit id "+ dto.getMediaId() +" geupdated - Du hast die Funktion update() im RatingController erreicht");
    }

    public Response delete(Request request) { //deleteRating

        String ratingId = request.extractIdAsString();

        Rating deletedRating = this.ratingService.deleteRating(ratingId);

        //validation des deleted Rating

        //Response mit deleted Rating im Body erstellen - Response 204 ist definiert, dass er keinen Body mitschickt!
        return new Response(Status.UNMARKED,  ContentType.TEXT_PLAIN , "Rating mit id " + deletedRating.getId() + " gelöscht. - Du hast die Funktion delete() im RatingController erreicht");
        //wenn Erfolg: Status: 204  Rating deleted
    }

    public Response likeRating(Request request) {

        // rating_id
        UUID rating_Id = request.extractIdAsUUID();

        // sender_name
        String senderName = request.extractNameFromHeader();

        LikedBy likedByDTO = new LikedBy();
        likedByDTO.setRatingId(rating_Id);
        likedByDTO.setSenderName(senderName);

        //UUID des gelikted Ratings zurück geben?
        UUID likedRatingID = this.ratingService.likeRating(likedByDTO);


        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Rating (with id "+ likedRatingID +") liked");
    }


    //nicht CRUD-konforme Funktionen:
    public Response confirmComment(Request request) {

        // extract rating ID
        UUID ratingId = request.extractIdAsUUID();

        // username des:der Ersteller:In aus auth-header holen
        String creatorName = request.extractNameFromHeader();

        //DTO erstellen
        CommentConfirm ratingDTO = new CommentConfirm();
        ratingDTO.setRatingId(ratingId);
        ratingDTO.setCreatorName(creatorName);

        //später: return Comment -> Nein, es ist keine Möglichkeit angegeben einen Comment zu "unconfirmen", somit sehe ich keinen Sinn darin etwas zurückzugeben. Selbst wenn würde die Rating Id reichen
        this.ratingService.confirmComment(ratingDTO);

        //Wenn bisher keine Exception geworfen wurde, erreichen wir die postitive Rückmeldung:
        return new Response(Status.OK, ContentType.TEXT_PLAIN, "Comment confirmed");
    }
}