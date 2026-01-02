package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.CommentConfirm;
import at.technikum.application.mrp.model.dto.LikedBy;
import at.technikum.application.mrp.model.dto.RatingCreated;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class RatingService {
    private RatingRepository ratingRepository;
    private MediaRepository mediaRepository;
    private UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository,
                         MediaRepository mediaRepository,
                         UserRepository userRepository
                         ) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    public RatingCreated createRating(RatingInput ratingDTO)
    {
        // DTO validieren -> Comment darf NULL sein!
        if(ratingDTO.getMediaId() == null
            || ratingDTO.getStars() == null
            || ratingDTO.getCreatorName() == null || ratingDTO.getCreatorName().equals(""))
        {
            throw new IllegalArgumentException("RatingDTO misses either stars, mediaId or creatorName");
        }

        // aus DTO -> Rating erstellen
        Rating rating = new Rating();
        rating.setId(UUID.randomUUID());
        rating.setStars(ratingDTO.getStars());
        rating.setComment(ratingDTO.getComment());
        rating.setConfirmed(false); //er wird ja neu erstellt --> noch nicht confirmed
        //media ist ein media Objekt -> es erhält nur die UUID, da nur diese Relevant für das Speichern des Ratings ist
        Media media = new Media();
        media.setId(ratingDTO.getMediaId());
        rating.setMedia(media);
        //user-id finden wir über den Namen aus dem Token
        rating.setCreatorId(this.userRepository.getIdViaName(ratingDTO.getCreatorName()));

        //Repo-Funktion aufrufen.
        Optional<Rating> createdRatingOpt = this.ratingRepository.create(rating);

        //Optional validieren
        if(!createdRatingOpt.isPresent())
        {
            throw new EntityNotSavedCorrectlyException("Rating not saved correctly");
        }

        /* Folgendes Probelm ist aufgetreten: Beim Serialisieren des Ratings (im Controller)
        * Ist das nicht möglich, da versucht wird das Media in Rating ebenfalls zu serialisieren.
        * Zu dem Media haben wir aber nicht alle Informationen (nur die ID)
        * Lösungesmöglichkeiten:
        * 1.) Tatsächlich das ganze Media in der DB abfragen
        *   -> ich mag diese Lösung nicht, da es mir wie ein unnötiger DB Zugriff vorkommt
        * 2.) Nicht einen Response, sondern ein DTO zurückgeben, welches nicht ein ganzes Media,
        * sondern nur die UUID des Medias enthält.
        *   -> Das Entspricht zwar nicht ganz der Schichtenlogik, die wir besprochen haben,
        *   kommt mir in diesem Fall aber wie die bessere Lösung vor?*/

        //Rating das vom Repo zurückgegeben wird befreien
        Rating createdRating = createdRatingOpt.get();


        // Validieren, dass dessen Inhalte mit denen die gespeichert werden sollten übereinstimmen
        if (!createdRating.getId().equals(rating.getId())
                || !createdRating.getMedia().getId().equals(rating.getMedia().getId())
                || createdRating.getStars() != rating.getStars()
                || !Objects.equals(createdRating.getComment(), rating.getComment())
                || createdRating.getConfirmed() != rating.getConfirmed()
                || !createdRating.getCreatorID().equals(rating.getCreatorID())
        ) {
            throw new EntityNotSavedCorrectlyException(
                    "saved Rating differs from rating that should have been saved");
        }


        //Das Rating in das passende RatingCreated DTO übertragen -> Damit via JSON zurückgegeben werden kann
        RatingCreated createdRatingDTO = new RatingCreated();
        createdRatingDTO.setRatingId(createdRating.getId());
        createdRatingDTO.setStars(createdRating.getStars());
        createdRatingDTO.setComment(createdRating.getComment());
        createdRatingDTO.setConfirmed(createdRating.getConfirmed());
        createdRatingDTO.setMediaId(createdRating.getMedia().getId()); //das ist die einzig relevante Zeile wegen der ich das mache
        createdRatingDTO.setCreatorId(createdRating.getCreatorID());

        //erstelltes zurückgeben
        return createdRatingDTO;
    }

    public UUID likeRating(LikedBy likedByDTO)
    {
        // Validierung
        if(likedByDTO.getSenderName().isEmpty() || likedByDTO.getSenderName().equals("")){
            throw new IllegalArgumentException("LikedByDTO misses senderName");
        }

        // kontrolle: User darf nicht eigenes Rating liken
        UUID senderID = userRepository.getIdViaName(likedByDTO.getSenderName());

        Optional<Rating> ratingToLike = this.ratingRepository.find(likedByDTO.getRatingId());
        if(!ratingToLike.isPresent())
        {
            throw new EntityNotFoundException("Rating to be liked not found!");
        }
        UUID creatorID = ratingToLike.get().getCreatorID();

        if(senderID.equals(creatorID))
        {
            throw new UnauthorizedException("Users can not like their own rating!");
        }

        // Kontrolle: User:in darf Rating nur 1x liken
            //also eigentlich wollen wir ein find() in einem LikeRepository machen und ein Empty Optional zurückbekommen
            //okay nach dem Umsetzen ist mir klar geworden, dass das unsinnig ist, da wir keine zusätzlichen Informationen
            // in der Tabelle haben -> ich denke eine boolean likedBy() funktion im RatingsRepo macht mehr Sinn
        boolean alreadyLiked = this.ratingRepository.likedBy(likedByDTO.getRatingId(), senderID);

        if(alreadyLiked)
        {
            throw new UnauthorizedException("A user can like the same rating only once!");
        }

        // Rating liken
        return this.ratingRepository.likeRating(likedByDTO.getRatingId(), senderID);
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
        deletedRating.setId(UUID.fromString(id));
        return deletedRating;
    }

    public void confirmComment(CommentConfirm commentDTO)
    {
        //commentDTO validieren
            //UUID muss bereits eine sein
            //username auf emptyness prüfen
        if(commentDTO.getCreatorName().isEmpty() || commentDTO.getCreatorName().equals(""))
        {
            throw new IllegalArgumentException("In order to confirm comment, Creator name in Auth Header can not be empty");
        }

        // user_id zu username herausfinden
        UUID creatorId = this.userRepository.getIdViaName(commentDTO.getCreatorName());

        // Sicherstellen, dass die Person die den Comment confirmed auch der:die Ersteller:In ist
            //find gibt es noch nicht, wird ein Optional von einem Rating zurückgeben
        Optional<Rating> ratingOpt = this.ratingRepository.find(commentDTO.getRatingId());

        if(!ratingOpt.isPresent())
        {
            throw new EntityNotFoundException("Rating whichs comment should be confirmed could not be found");
        }

        //creator_ids aus DB und Request vergleichen
        if(!creatorId.equals(ratingOpt.get().getCreatorID()))
        {
            throw new UnauthorizedException("Unauthorized - only the creator of a rating can confirm its comment. ");
        }

        //-> Comment confirmen mittels Repo-Funktion
        this.ratingRepository.confirm(commentDTO.getRatingId());

        //validieren -> Es kommt nichts zurück es gibt nichts zum validieren
    }
}
