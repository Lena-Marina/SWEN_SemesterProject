package at.technikum.application.mrp.model.util;

import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        rating.setComment("I like this Media a lot!");

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
        rating.setComment("I like this Media a lot!");

        // Act
        validator.removeCommentIfNotConfirmed(rating);

        // Assert
        assertEquals("I like this Media a lot!", rating.getComment(), "Comment of confirmed rating should remain unchanged");
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

    /*Die Methode validateMediaOrThrow() wird in fast jeder Methode des MediaService
     * genutzt. Außerdem ist sicherzugehen, dass ein valides Media Objekt
     * zurück gegeben wird, relevant + hier konnte ich Boundary analyse tests machen
     * für die Felder avg_score, ageRestriction und releaseYear in Media*/

    private Media createValidMedia() {
        Media media = new Media();
        media.setId(UUID.randomUUID());
        media.setTitle("Interstellar");
        media.setDescription("Sci-Fi Movie");
        media.setMediaType("movie");
        media.setReleaseYear(2014);
        media.setAgeRestriction(12);
        media.setCreatorID(UUID.randomUUID());

        media.setGenres(new ArrayList<>());
        media.setFavoritedBy(new ArrayList<>());
        media.setRatings(new ArrayList<>());

        media.setAverageScore(4.5f);

        return media;
    }

    @Test
    void given_valid_media_when_validateMediaOrThrow_then_no_exception_is_thrown() {
        // Arrange
        Media media = createValidMedia();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validateMediaOrThrow(media));
    }

    @Test
    void given_null_media_when_validateMediaOrThrow_then_throws_exception() {
        // Act & Assert
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(null)); //null wird statt Media übergeben

        assertEquals("Media must not be null", ex.getMessage());
    }

    @Test
    void given_invalid_mediaType_when_validateMediaOrThrow_then_throws_exception() {
        // Arrange
        Media media = createValidMedia();
        media.setMediaType("book");

        // Act
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media));

        // Assert
        assertEquals("mediaType must be 'series', 'movie' or 'game'", ex.getMessage());
    }

    @Test //erlaubt ist alles >= 0 und <=18 -> Boundarys -1 und 19
    void given_ageRestriction_out_of_bounds_when_validateMediaOrThrow_then_throws_exception() {
        // Arrange
        Media media1 = createValidMedia();
        media1.setAgeRestriction(19);

        Media media2 = createValidMedia();
        media2.setAgeRestriction(-1);

        // Act
        IllegalArgumentException ex1 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media1));

        IllegalArgumentException ex2 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media2));

        // Assert
        assertEquals("ageRestriction must be between 0 and 18", ex1.getMessage());
        assertEquals("ageRestriction must be between 0 and 18", ex2.getMessage());
    }

    @Test //release Year muss zwischen 0 und der aktuellen Jahreszahl liegen -> Boundarys nächstesJahrJahreszahl und -1
    void given_releaseYear_out_of_bounds_when_validateMediaOrThrow_then_throws_exception() {
        // Arrange
        int currentYear = LocalDate.now().getYear();
        Media media1 = createValidMedia();
        media1.setReleaseYear(currentYear + 1);

        Media media2 = createValidMedia();
        media2.setReleaseYear(-1);

        // Act
        IllegalArgumentException ex1 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media1));

        IllegalArgumentException ex2 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media2));

        // Assert
        assertTrue(ex1.getMessage().startsWith("releaseYear must be between 0 and " + currentYear));
        assertTrue(ex2.getMessage().startsWith("releaseYear must be between 0 and " + currentYear));
    }

    @Test //Average score muss zwischen 0 und 5 liegen -> Boundarys 5.1 und -0.1
    void given_averageScore_above_5_when_validateMediaOrThrow_then_throws_exception() {
        // Arrange
        Media media1 = createValidMedia();
        media1.setAverageScore(5.1f);

        Media media2 = createValidMedia();
        media2.setAverageScore(-0.1f);

        // Act
        IllegalArgumentException ex1 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media1));

        IllegalArgumentException ex2 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media2));

        // Assert
        assertEquals("averageScore must be between 0 and 5", ex1.getMessage());
        assertEquals("averageScore must be between 0 and 5", ex2.getMessage());
    }

    @Test //Alle listen auf initialisierung testen
    void given_uninitialized_list_when_validateMediaOrThrow_then_throws_exception() {
        // Arrange
        Media media1 = createValidMedia();
        media1.setGenres(null);

        Media media2 = createValidMedia();
        media2.setFavoritedBy(null);

        Media media3 = createValidMedia();
        media3.setRatings(null);

        // Act
        IllegalArgumentException ex1 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media1));

        IllegalArgumentException ex2 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media2));

        IllegalArgumentException ex3 =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validateMediaOrThrow(media3));

        // Assert
        assertEquals("Genres list must be initialized", ex1.getMessage());
        assertEquals("FavoritedBy list must be initialized", ex2.getMessage());
        assertEquals("Ratings list must be initialized", ex3.getMessage());
    }







}
