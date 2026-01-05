package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.RecommendationRequest;
import at.technikum.application.mrp.model.helper.RecommendationHelper;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;
import at.technikum.application.mrp.service.util.RatingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MediaServiceTests {
    /*
     * Teil der Business Logik ist es zu prüfen ob der:die Anfragesteller:in ein Medium
     * löschen und updaten darf.
     *
     * Dies wird im MediaService in den Funktionen deleteMedia() und updateMedia()
     * überprüft.
     *
     * Dafür gibt es die Funktion protected void checkPermission(UUID mediaID, String requesterName)
     * im MediaService.
     *
     * Diese holt sich UUIDS aus dem Repository -> das will ich hier nicht testen
     * Sondern nur wie sich die Funktion verhält, wenn die selbe vs unterschiedliche UUIDS verglichen werden
     * */

    @Mock
    MediaRepository mediaRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RatingRepository ratingRepository;

    @Mock
    RatingValidator ratingValidator;

    @InjectMocks
    MediaService mediaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void given_same_uuid_when_checkPermission_then_no_exception() {
        UUID mediaID = UUID.randomUUID();
        UUID userID = UUID.randomUUID();
        String requesterName = "Lena1";

        // Mocks -> hier gehe ich sicher, dass die gleiche UUID zurück kommt:
        when(userRepository.getIdViaName(requesterName)).thenReturn(userID);
        when(mediaRepository.getCreatorIdViaMediaEntryID(mediaID)).thenReturn(userID);

        // Act & Assert
        assertDoesNotThrow(() -> mediaService.checkPermission(mediaID, requesterName));
    }

    @Test
    void given_different_uuid_when_checkPermission_then_throws_UnauthorizedException() {
        UUID mediaID = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID userID = UUID.fromString("20000000-0000-0000-0000-000000000002");
        UUID creatorID = UUID.fromString("30000000-0000-0000-0000-000000000003"); //für den winzigen Fall, dass mir randomUUID die selbe generiert werden würde -> dann würde der Test ja failen
        String requesterName = "Lena1";

        // Mocks
        when(userRepository.getIdViaName(requesterName)).thenReturn(userID);
        when(mediaRepository.getCreatorIdViaMediaEntryID(mediaID)).thenReturn(creatorID);

        // Act & Assert
        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> mediaService.checkPermission(mediaID, requesterName)
        );

        assertEquals("Only the creator of a media entry may perform this action", ex.getMessage());
    }

    /*
    Für die Recommendation-Logik habe ich zwei verschiedene SQL Abfragen, abhängig davon
    ob Empfehlungen nach content oder genre gesucht wird. Dafür habe ich auch zwei
    verschiedene Repository-Funktionen geschrieben, welche jeweils eine der SQL Abfragen
    enthalten:  ratingRepository.getTypeWithStars() und ratingRepository.getGenresWithStars()

    In der Entwicklung hatte ich dabei einen Fehler, bei dem ich die falsche Funktion
    aufgerufen habe. Diesen habe ich zwar in der Zwischenzeit behoben,
    aber ich denke, dass es trotzdem ein sinnvoller Test ist, sollte in der Zukunft
    nochmal daran gearbeitet werden.

    Da ich den aufruf von zwei Funktionen testen möchte, hallte ich hier zwei Tests für
    sinnvoll, obwohl sich die beiden Tests natürlich sehr ähneln

    * gehört zur Business Logik: Recommendation system based on rating history & similarity
    */
    @Test
    void given_type_content_when_getRecommendation_then_calls_getTypeWithStars() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID ratingId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        RecommendationRequest dto = new RecommendationRequest();
        dto.setUserId(userId);
        dto.setType("content");

        Rating rating = new Rating();
        rating.setRatingId(ratingId);

        when(ratingRepository.findAllFrom(userId))
                .thenReturn(List.of(rating));

        RecommendationHelper helper = new RecommendationHelper();
        helper.setName("movie");
        helper.setStars(5);

        when(ratingRepository.getTypeWithStars(ratingId))
                .thenReturn(helper);

        when(mediaRepository.findAllWithType("movie"))
                .thenReturn(List.of()); // Ergebnis egal

        // Act
        mediaService.getRecommendation(dto);

        // Assert
        verify(ratingRepository).getTypeWithStars(ratingId);
        verify(ratingRepository, never()).getGenresWithStars(any());
    }

    @Test
    void given_type_genre_when_getRecommendation_then_calls_getGenresWithStars() {
        // arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        UUID ratingId = UUID.fromString("00000000-0000-0000-0000-000000000004");

        RecommendationRequest dto = new RecommendationRequest();
        dto.setUserId(userId);
        dto.setType("genre");

        Rating rating = new Rating();
        rating.setRatingId(ratingId);

        when(ratingRepository.findAllFrom(userId))
                .thenReturn(List.of(rating));

        RecommendationHelper helper = new RecommendationHelper();
        helper.setName("HORROR");
        helper.setStars(4);

        when(ratingRepository.getGenresWithStars(ratingId))
                .thenReturn(List.of(helper));

        when(mediaRepository.findAllWithGenre("HORROR"))
                .thenReturn(List.of());

        // act
        mediaService.getRecommendation(dto);

        // assert
        verify(ratingRepository).getGenresWithStars(ratingId);
        verify(ratingRepository, never()).getTypeWithStars(any());
    }

    /*Validation eines Mediums testen -> hier könnte ich boundary limit gut umsetzen
     * (in age_restriction und release_year) */

    /*Avg Score berechnung validieren -> Aber eigentlich passiert das interessante
    hier in der DB, welche zu testen nicht meine Verantwortung ist...
    in der Business logik mache ich nur eine einfache division, die muss ich denke ich
    eher nicht testen
    * */
}







