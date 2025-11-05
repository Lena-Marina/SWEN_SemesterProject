package at.technikum.application.mrp.service;

import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.repository.MediaRepository;

public class RatingService {
    private MediaRepository mediaRepository;

    public RatingService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public void createRating(RatingInput rating_dto)
    {
        //logik

        //Repo-Funktion aufrufen.
    }

    public void likeRating(String id)
    {
        //id validieren.

        //Repo Funktion aufrufen

        //eventuell geliktes Rating zurückgeben.
    }

    public void changeRating(RatingInput rating_dto)
    {
        //dto validieren

        //Repo Funktion aufrufen
        //check if Rating belongs to user -> ich brauche als parameter auch noch die User ID

        //etwas zurückgeben
    }

    public Rating deleteRating(String id)
    {
        //Repofunktion aufrufen!
        //check if Rating belongs to user -> ich brauche als parameter sowohl User Id als auch Rating ID

        Rating deletedRating = new Rating();
        deletedRating.setId(id);
        return deletedRating;
    }

    public /*Comment*/ void confirmComment(String id)
    {
        //Repofunktin aufrufen

        //validieren

        //Comment zurückgeben
    }
}
