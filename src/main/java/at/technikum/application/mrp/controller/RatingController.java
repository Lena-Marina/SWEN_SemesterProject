package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.*;
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
        RatingChange ratingChangeDTO = toObject(request.getBody(), RatingChange.class);

        //id extrahieren und dto zufügen
        ratingChangeDTO.setMediaId(request.extractIdAsUUID());

        //name zu DTO hinzufügen
        ratingChangeDTO.setCreatorName(request.extractNameFromHeader());

        //Aufruf Service Funktion
        Rating updatedRating = this.ratingService.changeRating(ratingChangeDTO);

        //Weil im unterricht besprochen: Abweichung von Spezifikation -> es wird das geupdatete Rating retourniert
        return json(updatedRating, Status.OK);
    }

    public Response delete(Request request) { //deleteRating

        UUID ratingId = request.extractIdAsUUID();

        //nur der:die Ersteller:In darf Rating auch löschen!
        String creatorName = request.extractNameFromHeader();


        Rating deletedRating = this.ratingService.deleteRating(ratingId, creatorName);

        //Abweichung von Spezifikation: wir schicken das Deleted Rating zurück, daher anderer Satuscode
        return json(deletedRating, Status.OK);
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