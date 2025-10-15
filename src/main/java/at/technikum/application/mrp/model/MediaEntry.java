package at.technikum.application.mrp.model;

import java.util.List;

public class MediaEntry {
    //Ids in der Datenbank und Somit auch heir sollten nicht einfach selbstinkrementierende Ints sein,
    //sondern z.B. UUIDs wir werden uns mit Strings behelfen, welche UUIDS repräsentieren
    //es gibt in der Realität auch einen Datentyp für UUIDS
    public class Media {
        private String id;
        private String title;
        private String description;
        private String mediaType; // z.B. "movie", "series", "book"
        private int releaseYear;
        private List<String> genres;
        private int ageRestriction;

        private List<User> favoritedBy; // Users, die dieses Medium als Favorit markiert haben
        private List<Rating> ratings;

        // Getter & Setter
    }

}
