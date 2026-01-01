package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.dto.MediaInput;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaRepository implements MrpRepository<Media> {
    private final ConnectionPool connectionPool;

    public MediaRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    @Override
    public Optional<Media> find(UUID id) {
        String sql = "SELECT * FROM media_entry WHERE media_id = ?";

        try (PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return Optional.empty(); // User existiert nicht
            }

            return Optional.of(mapToMedia(rs));
        } catch (Exception e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    private Media mapToMedia(ResultSet rs) throws SQLException {
        Media media = new Media();

        // UUIDs aus DB auslesen
        String mediaIdStr = rs.getString("media_id");
        if (mediaIdStr != null) {
            media.setId(UUID.fromString(mediaIdStr));
        }

        String creatorIdStr = rs.getString("creator_id");
        if (creatorIdStr != null) {
            media.setCreatorID(UUID.fromString(creatorIdStr));
        }

        // einfache Strings
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setMediaType(rs.getString("type"));

        // int-Werte
        media.setReleaseYear(rs.getInt("release_year"));
        media.setAgeRestriction(rs.getInt("age_restriction"));

        // Todo: leere Listen befüllen initialisieren
        media.setGenres(new ArrayList<>());
        media.setFavoritedBy(new ArrayList<>());
        media.setRatings(new ArrayList<>());

        return media;
    }

    @Override
    public List<Media> findAll() {
        return List.of();
    }

    @Override
    public Optional<Media> create(Media object) {

        try (Connection conn = connectionPool.getConnection();) {
            conn.setAutoCommit(false);

            // media_entry
            String sql = """
                        INSERT INTO media_entry
                        (media_id, creator_id, title, description, type, release_year, age_restriction)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // ? befüllen
                stmt.setObject(1, object.getId());
                stmt.setObject(2, object.getCreatorID());
                stmt.setString(3, object.getTitle());
                stmt.setString(4, object.getDescription());
                stmt.setString(5, object.getMediaType());
                stmt.setInt(6, object.getReleaseYear());
                stmt.setInt(7, object.getAgeRestriction());

                //statement ausführen
                stmt.executeUpdate();
            }

            if (object.getGenres() != null) {
                for (Genre genre : object.getGenres()) { //für jedes Genre einen Eintrag in is_genre erstellen
                    //zunächst muss die genre_id ermittelt werden
                    UUID genreId;
                    try (PreparedStatement stmt = conn.prepareStatement("SELECT genre_id FROM genre WHERE name = ?")) {
                        // ? befüllen
                        stmt.setString(1, genre.name());

                        // Statement ausführen
                        ResultSet rs = stmt.executeQuery();

                        if (!rs.next()) {
                            throw new EntityNotSavedCorrectlyException(
                                    "Genre not found: " + genre.name());
                        }
                        genreId = rs.getObject("genre_id", UUID.class);
                    }
                    //jetzt wird der is_genre Eintrag erstellt
                    try (PreparedStatement stmt =
                                 conn.prepareStatement("INSERT INTO is_genre (media_id, genre_id) VALUES (?, ?)")) {

                        // ? befüllen
                        stmt.setObject(1, object.getId());
                        stmt.setObject(2, genreId);
                        // statement ausführen
                        stmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            return find(object.getId()); /*ich sollte eigentlich jetzt das Objekt aus der DB abfragen und ds dann zurückgeben damit auch wirklich geprüft wird ob es abgespeichert wurde*/

        } catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException(e.getMessage());
        }
    }

    public String getCreatorNameByMediaID(UUID mediaID) {
        String sql = """
                SELECT u.username
                FROM media_entry m
                JOIN users u ON m.creator_id = u.user_id
                WHERE m.media_id = ?
                """;

        try (PreparedStatement stmt = this.connectionPool
                .getConnection()
                .prepareStatement(sql)) {

            stmt.setObject(1, mediaID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

            throw new EntityNotFoundException(
                    "Media_Entry with Id " + mediaID + " not found"
            );

        } catch (SQLException e) {
            throw new EntityNotFoundException(
                    "Error fetching creator name for media_id " + mediaID,
                    e
            );
        }
    }


    @Override
    public Optional<Media> update(Media object) {

        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false); //damit nicht jedes SQL Statement sofort commited wird, sondern wir entweder alles ändern oder gar nichts

            // 1. is_genre Verbindungen löschen
            try (PreparedStatement stmt =
                         conn.prepareStatement("DELETE FROM is_genre WHERE media_id = ?")) {

                stmt.setObject(1, object.getId());
                stmt.executeUpdate();
            }

            // 2. media_entry aktualisieren
            String sql = """
                        UPDATE media_entry
                        SET title = ?, description = ?, type = ?, release_year = ?, age_restriction = ?
                        WHERE media_id = ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, object.getTitle());
                stmt.setString(2, object.getDescription());
                stmt.setString(3, object.getMediaType());
                stmt.setInt(4, object.getReleaseYear());
                stmt.setInt(5, object.getAgeRestriction());
                stmt.setObject(6, object.getId());

                int updatedRows = stmt.executeUpdate();
                if (updatedRows == 0) {
                    throw new EntityNotFoundException(
                            "Media with id " + object.getId() + " not found");
                }
            }

            // 3. neue is_genre Verbindungen herstellen
            if (object.getGenres() != null) {
                for (Genre genre : object.getGenres()) {

                    UUID genreId;
                    try (PreparedStatement stmt =
                                 conn.prepareStatement("SELECT genre_id FROM genre WHERE name = ?")) {

                        stmt.setString(1, genre.name());
                        ResultSet rs = stmt.executeQuery();

                        if (!rs.next()) {
                            throw new EntityNotSavedCorrectlyException(
                                    "Genre not found: " + genre.name());
                        }
                        genreId = rs.getObject("genre_id", UUID.class);
                    }

                    try (PreparedStatement stmt =
                                 conn.prepareStatement(
                                         "INSERT INTO is_genre (media_id, genre_id) VALUES (?, ?)")) {

                        stmt.setObject(1, object.getId());
                        stmt.setObject(2, genreId);
                        stmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            return find(object.getId());

        } catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException(e.getMessage());
        }
    }

    public UUID getCreatorIdViaMediaEntryID(UUID mediaId) {
        String sql = "SELECT creator_id FROM media_entry WHERE media_id = ?";

        try (PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql)) {
            // ? befüllen
            stmt.setObject(1, mediaId);

            // Statement ausführen
            ResultSet rs = stmt.executeQuery();

            //Ergbenis auswerten
            if (rs.next()) {
                return rs.getObject("creator_id", UUID.class);
            }

            throw new EntityNotFoundException("MediaEntry with id '" + mediaId + "' not found");
        } catch (SQLException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }


    @Override
    public Media delete(UUID mediaID) {
        /* gibt ein Media zurück, damit das Löschen rückgängig gemacht werden kann
         * ich muss also vor dem Löschen alle Informationen über das Medium,
         * inklusive den Listen favoritedBy und ratings in ein Media speichern,
         * welches am Ende zurück gegeben wird*/

        // 1. Media vor dem Löschen laden (Basisinfos)
        Media deletedMedia = this.find(mediaID)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Media with id " + mediaID + " not found"));

        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false); // alles in einer Transaktion (wenn wir einen fehler haben wird nichts in der DB commited)

            // 2️. Genres aus is_genre JOIN genre (weil das die namen enthält) laden
            List<Genre> genres = new ArrayList<>();
            String sqlGenres = """
            SELECT g.name
            FROM is_genre ig
            JOIN genre g ON ig.genre_id = g.genre_id
            WHERE ig.media_id = ?
        """;
            try (PreparedStatement stmt = conn.prepareStatement(sqlGenres)) {
                stmt.setObject(1, mediaID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    genres.add(Genre.valueOf(rs.getString("name")));
                }
            }
            deletedMedia.setGenres(genres);

            // 3. FavoritedBy UUIDs aus favorites laden
            List<UUID> favorites = new ArrayList<>();
            String sqlFavorites = "SELECT user_id FROM favorites WHERE media_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlFavorites)) {
                stmt.setObject(1, mediaID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    favorites.add(rs.getObject("user_id", UUID.class));
                }
            }
            deletedMedia.setFavoritedBy(favorites);

            // 4. Ratings aus ratings laden
            List<Rating> ratings = new ArrayList<>();
            String sqlRatings = """
            SELECT rating_id, stars, comment, confirmed, creator_id
            FROM ratings
            WHERE media_id = ?
        """;
            try (PreparedStatement stmt = conn.prepareStatement(sqlRatings)) {
                stmt.setObject(1, mediaID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Rating rating = new Rating();
                    rating.setId(rs.getObject("rating_id", UUID.class));
                    rating.setStars(rs.getInt("stars"));
                    rating.setComment(rs.getString("comment"));
                    rating.setConfirmed(rs.getBoolean("confirmed"));
                    rating.setCreatorId(rs.getObject("creator_id", UUID.class));
                    rating.setMedia(deletedMedia);
                    ratings.add(rating);
                }
            }
            deletedMedia.setRatings(ratings);

            // 5️. Einträge in is_genre löschen
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM is_genre WHERE media_id = ?")) {
                stmt.setObject(1, mediaID);
                stmt.executeUpdate();
            }

            // 6. Einträge in ratings löschen
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM ratings WHERE media_id = ?")) {
                stmt.setObject(1, mediaID);
                stmt.executeUpdate();
            }

            // 7. Einträge in favorites löschen
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM favorites WHERE media_id = ?")) {
                stmt.setObject(1, mediaID);
                stmt.executeUpdate();
            }

            // 8. Eintrag in media_entry löschen
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM media_entry WHERE media_id = ?")) {
                stmt.setObject(1, mediaID);
                stmt.executeUpdate();
            }

            // 9️. Transaktion committen
            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting media: " + e.getMessage(), e);
        }

        // 10. Media zurückgeben
        return deletedMedia;
    }
}
