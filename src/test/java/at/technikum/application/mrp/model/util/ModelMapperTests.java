package at.technikum.application.mrp.model.util;

import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.dto.MediaInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ModelMapperTests {
    /*
    * Für die Funktionen createMedia() und updateMedia() muss ich ein MediaInput (DTO)
    * aus ein Media Mappen.
    * Da dies 2x passiert, habe ich dafür eine eigene Funktion im ModelMapper erstellt.
    * Wenn hier (verborgen in einer Helfer-Klasse ModelMapper etwas nicht funktioniert,
    * wäre es meiner Ansicht nach ein Fehler, der länger zum entdecken brauchen würde,
    * daher teste ich die Methode (auch wenn es sich um einen sehr einfachen Test handelt)
    */

    private ModelMapper mapper; // oder wie die Klasse heißt

    @BeforeEach
    void setUp() {
        mapper = new ModelMapper();
    }

    @Test
    void test_given_valid_mediaInput_when_mapToMedia_then_all_fields_are_mapped_correctly() {
        // Arrange
        UUID mediaId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID creatorId = UUID.fromString("20000000-0000-0000-0000-000000000002");

        MediaInput dto = new MediaInput();
        dto.setId(mediaId);
        dto.setTitle("Interstellar");
        dto.setDescription("Sci-Fi Movie");
        dto.setReleaseYear(2014);
        dto.setAgeRestriction(12);
        dto.setMediaType("movie");
        dto.setGenres(List.of(Genre.SCI_FI, Genre.DRAMA));

        // Act
        Media media = mapper.mapToMedia(dto, creatorId);

        // Assert - ein Media wurde erstellt:
        assertNotNull(media);

        //Assert - alles aus dem DTO wurde übernommen:
        assertEquals(mediaId, media.getId());
        assertEquals("Interstellar", media.getTitle());
        assertEquals("Sci-Fi Movie", media.getDescription());
        assertEquals(2014, media.getReleaseYear());
        assertEquals(12, media.getAgeRestriction());
        assertEquals("movie", media.getMediaType());
        assertEquals(List.of(Genre.SCI_FI, Genre.DRAMA), media.getGenres());
        assertEquals(creatorId, media.getCreatorID());

        //Assert - im DTO unbekannte Listen wurden auch erstellt, sind aber leer:
        assertNotNull(media.getFavoritedBy());
        assertTrue(media.getFavoritedBy().isEmpty());
        assertNotNull(media.getRatings());
        assertTrue(media.getRatings().isEmpty());
    }
}

