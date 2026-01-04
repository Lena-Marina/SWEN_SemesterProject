package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    UserRepository userRepository;

    @Mock
    MediaRepository mediaRepository;

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
        UUID mediaID   = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID userID    = UUID.fromString("20000000-0000-0000-0000-000000000002");
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
}
