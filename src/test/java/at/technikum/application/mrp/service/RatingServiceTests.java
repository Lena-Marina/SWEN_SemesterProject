package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.RatingInput;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Executable;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTests {
    /* Als Teil der Business-Logik muss ich sicherstellen, dass der:die selbe User:in
     * das selbe Medium nur 1x bewerten kann.
     *
     * Das passiert im RatingService in der Methode createRating()
     * in welchem über die RatingRepository Methode alreadyRatedByUser() abgefragt wird,
     * ob bereits ein Rating mit der selben creator_id und media_id existiert.
     *
     * Ich möchte daher mit Tests sichergehen, dass in createRating() eine Exception geworfen wird, wenn
     * Bereits ein ensprechendes Rating existiert und keine, wenn ein solches nicht existiert*/

    @Mock
    RatingRepository ratingRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    RatingService ratingService;

    @Test
    void test_given_user_already_rated_media_when_createRating_then_throws_UnauthorizedException() {
        // setup | arrange
        UUID mediaId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID userId = UUID.fromString("20000000-0000-0000-0000-000000000002");

        RatingInput dto = new RatingInput();
        dto.setMediaId(mediaId);
        dto.setStars(4);
        dto.setCreatorName("Lena2");

        when(userRepository.getIdViaName("Lena2")).thenReturn(userId);
        when(ratingRepository.alreadyRatedByUser(mediaId, userId)).thenReturn(true);

        // call | act & assertion | assert
        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> ratingService.createRating(dto)
        );

        // "nur" assertion | assert
        assertEquals("User can only rate same media once", ex.getMessage());
        verify(ratingRepository, never()).create(any());
    }

    @Test //Gegentest
    void test_given_user_has_not_rated_media_when_createRating_then_throws_no_Exception()
    {
        // setup | arrange
        UUID mediaId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID userId = UUID.fromString("20000000-0000-0000-0000-000000000002");

        RatingInput dto = new RatingInput();
        dto.setMediaId(mediaId);
        dto.setStars(5);
        dto.setCreatorName("Lena2");
        dto.setComment("Great movie");

        when(userRepository.getIdViaName("Lena2")).thenReturn(userId);
        when(ratingRepository.alreadyRatedByUser(mediaId, userId)).thenReturn(false);
        /*Da für den fall, dass der:die User:in das media noch nicht bewertet hat,
        * die ratingRepository-Funktion create() aufgerufen wird (aber das hier
        * ja nicht wirkich passieren soll) müssen wir sagen, was diese zurückgeben soll
        * egal was übergeben wird (any()) gib ein Optional das aus den infos die du in
        * any() bekommen hast besteht, zurück
        * */
        when(ratingRepository.create(any()))
                .thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        // call | act
        Rating result = ratingService.createRating(dto);

        // assertion | assert
        assertNotNull(result);
        assertEquals(mediaId, result.getMediaID());
        assertEquals(userId, result.getCreatorID());
        assertEquals(5, result.getStars());
        assertEquals("Great movie", result.getComment());
        assertFalse(result.getConfirmed());

        verify(ratingRepository).create(any(Rating.class));
    }




    /* in getRecommendation() testen ob eh nur die zwei getType() Ergebnisse zugelassen werden
    *
    * gehört zur business Logik: Recommendation system based on rating history & similarity
    * */

}
