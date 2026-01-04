package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.*;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;
import at.technikum.application.mrp.service.util.RatingValidator;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class RatingService {
    private RatingRepository ratingRepository;
    private UserRepository userRepository;
    private RatingValidator ratingValidator;

    public RatingService(RatingRepository ratingRepository,
                         UserRepository userRepository,
                         RatingValidator ratingValidator
                         ) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.ratingValidator = ratingValidator;
    }

    public Rating createRating(RatingInput ratingDTO)
    {
        // DTO validieren -> Comment darf NULL sein!
        if(ratingDTO.getMediaId() == null
            || ratingDTO.getStars() == null
            || ratingDTO.getCreatorName() == null || ratingDTO.getCreatorName().equals(""))
        {
            throw new IllegalArgumentException("RatingDTO misses either stars, mediaId or creatorName");
        }

        //prüfen ob diese:r User:in bereits dieses Medium gerated hat, denn wenn das so ist, darf er:sie das nicht mehr tun!
        UUID userId = this.userRepository.getIdViaName(ratingDTO.getCreatorName());
        boolean alreadyRated = this.ratingRepository.alreadyRatedByUser(ratingDTO.getMediaId(), userId);
        if(alreadyRated){
            throw new UnauthorizedException("User can only rate same media once");
        }

        // aus DTO -> Rating erstellen
        Rating rating = new Rating();
        rating.setRatingId(UUID.randomUUID());
        rating.setStars(ratingDTO.getStars());
        rating.setComment(ratingDTO.getComment());
        rating.setConfirmed(false); //er wird ja neu erstellt -> noch nicht confirmed
        rating.setMediaId(ratingDTO.getMediaId());
        rating.setCreatorId(userId); //user-id habe ich ja oben schon geholt

        //Repo-Funktion aufrufen.
        Optional<Rating> createdRatingOpt = this.ratingRepository.create(rating);

        //Optional validieren
        if(!createdRatingOpt.isPresent())
        {
            throw new EntityNotSavedCorrectlyException("Rating not saved correctly");
        }

        //Rating das vom Repo zurückgegeben wird befreien
        Rating createdRating = createdRatingOpt.get();


        // Validieren, dass dessen Inhalte mit denen die gespeichert werden sollten übereinstimmen
        if (!createdRating.getRatingId().equals(rating.getRatingId())
                || !createdRating.getMediaID().equals(rating.getMediaID())
                || createdRating.getStars() != rating.getStars()
                || !Objects.equals(createdRating.getComment(), rating.getComment())
                || createdRating.getConfirmed() != rating.getConfirmed()
                || !createdRating.getCreatorID().equals(rating.getCreatorID())
        ) {
            throw new EntityNotSavedCorrectlyException(
                    "saved Rating differs from rating that should have been saved");
        }

        //hier gebe ich schon jedenfalls den Comment wieder mit

        //erstelltes zurückgeben
        return createdRating;
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

    public Rating changeRating(RatingChange ratingDTO)
    {
        //dto validieren
        if(ratingDTO.getMediaId() == null
        || ratingDTO.getStars() == null
        || ratingDTO.getCreatorName() == null || ratingDTO.getCreatorName().equals("")
        //comment ist optional -> darf Null sein!
        ){
            throw new IllegalArgumentException("RatingDTO misses either stars, mediaId or creatorName");
        }

        // Anfragesteller:in muss ident sein mit rating_creator
        UUID changerID = userRepository.getIdViaName(ratingDTO.getCreatorName());
        Optional<Rating> ratingOpt = this.ratingRepository.find(ratingDTO.getMediaId());
        if(!ratingOpt.isPresent())
        {
            throw new EntityNotFoundException("Rating not found!");
        }
        if(!changerID.equals(ratingOpt.get().getCreatorID()))
        {
            throw new UnauthorizedException("Users can not change other user's ratings!");
        }

        //Repository erwartet ein RatingObjekt, daher DTO umwandeln
        Rating rating = new Rating();
        rating.setRatingId(ratingOpt.get().getRatingId()); //brauche ich um es zu finden
        rating.setStars(ratingDTO.getStars()); //brauche ich, weil möglicherweise geändert
        rating.setComment(ratingDTO.getComment()); //brauche ich, weil möglicherweise geändert
        rating.setConfirmed(false); // ich gehe davon aus, dass der geupdatete Kommentar auch erst wieder bestätigt werden muss


        //Repo Funktion aufrufen
        Optional<Rating> changedRatingOpt = this.ratingRepository.update(rating);

        if(!changedRatingOpt.isPresent()){
            throw new EntityNotSavedCorrectlyException("Rating not saved correctly");
        }

        Rating changedRating = changedRatingOpt.get();

        //Kommentar entfernen, falls nicht confirmed
        ratingValidator.removeCommentIfNotConfirmed(changedRating);

        return changedRating;
    }

    public Rating deleteRating(UUID ratingID, String creatorName)
    {
        //String validieren
        if(creatorName == null || creatorName.equals(""))
        {
            throw new IllegalArgumentException("Token misses senderName");
        }
        //senderId herausfinden
        UUID senderID = userRepository.getIdViaName(creatorName);

        //creatorID herausfinden
        Optional<Rating> ratingOpt = this.ratingRepository.find(ratingID);
        if(!ratingOpt.isPresent())
        {
            throw new EntityNotFoundException("Rating not found!");
        }
        UUID creatorID = ratingOpt.get().getCreatorID();

        //Darf nur gelöscht werden, wenn sender = creator
        if(!creatorID.equals(senderID))
        {
            throw new UnauthorizedException("Users can not delete other user's ratings!");
        }

        //Repofunktion aufrufen und gleich retournieren -> keine Validation des zurück erhaltenen Objektes, weil ich es selbst zurückgeben möchte wenn Teile fehlen, da ich dann zumindest einen Teil wieder herstellen kann
        return this.ratingRepository.delete(ratingID);
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
