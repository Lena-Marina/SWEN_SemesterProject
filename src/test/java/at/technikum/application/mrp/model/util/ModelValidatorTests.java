package at.technikum.application.mrp.model.util;

import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.service.util.RatingValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModelValidatorTests {

    private final ModelValidator validator = new ModelValidator();

    /*
     *   Tests aus den Business Logik Requirements die die Media Service relevanz haben:
     * "Comments require confirmation before public visibility" -> getFilteredMedium, getRecommendations, updateMedia, deleteMedia (ratings sind alle gelöscht), getMediaByID, createMedia (kann ja noch gar keine Ratings haben)
     *           -> dafür wurde jetzt eigene Funktion geschrieben: removeCommentIfNotConfirmed();
     * */
    //Tests für einzelne Ratings
    @Test
    void given_unconfirmed_rating_when_removeCommentIfNotConfirmed_then_comment_is_cleared() {
        // Arrange
        Rating rating = new Rating();
        rating.setConfirmed(false);
        rating.setComment("Sensitive comment");

        // Act
        validator.removeCommentIfNotConfirmed(rating);

        // Assert
        assertEquals("", rating.getComment(), "Comment of unconfirmed rating should be cleared");
    }

    @Test
    void given_confirmed_rating_when_removeCommentIfNotConfirmed_then_comment_remains() {
        // Arrange
        Rating rating = new Rating();
        rating.setConfirmed(true);
        rating.setComment("Important comment");

        // Act
        validator.removeCommentIfNotConfirmed(rating);

        // Assert
        assertEquals("Important comment", rating.getComment(), "Comment of confirmed rating should remain unchanged");
    }

    //Tests für Media
    @Test
    void given_unconfirmed_rating_when_removeCommentsIfNotConfirmedFromMedia_then_comment_is_cleared() {
        // Arrange
        Rating r1 = new Rating();
        r1.setConfirmed(false);
        r1.setComment("This should be removed");

        Rating r2 = new Rating();
        r2.setConfirmed(true);
        r2.setComment("This should stay");

        Media media = new Media();
        media.setRatings(List.of(r1, r2));

        // Act
        validator.removeCommentsIfNotConfirmedFromMedia(media);

        // Assert
        assertEquals("", r1.getComment(), "Comment of unconfirmed rating should be cleared");
        assertEquals("This should stay", r2.getComment(), "Comment of confirmed rating should remain unchanged");
    }

    @Test
    void given_no_ratings_when_removeCommentsIfNotConfirmedFromMedia_then_nothing_happens() {
        // Arrange
        Media media = new Media();
        media.setRatings(null);

        // Act
        validator.removeCommentsIfNotConfirmedFromMedia(media);

        //Assert
        assertNull(media.getRatings(), "Ratings should still be null");
    }
}
