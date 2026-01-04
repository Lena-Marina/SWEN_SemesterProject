package at.technikum.application.mrp.model.util;

import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.RatingInput;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class ModelMapper {
    public Media mapToMedia(ResultSet rs) throws SQLException {
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

    public User mapToUser(ResultSet rs) throws SQLException{
        User user = new User();

        String uuidString = rs.getString("user_id");
        if (uuidString != null) {
            user.setId(UUID.fromString(uuidString));
        }

        // einfache Strings
        //DEBUGGING
        System.out.println("---------------------------------");
        System.out.println("DEBUG in UserRepository::mapToUser() ");
        System.out.println("DEBUG: username = " + rs.getString("username"));

        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("hashed_pw"));
        user.setEmail(rs.getString("email"));

        //fav Genre ist ein Enum, daher der extra Wrap - Ein Enum darf niemals Null sein, deshalb brauche ich die extra Abfrage!
        String favGenre = rs.getString("fav_genre");
        if (favGenre != null) {
            user.setFavoriteGenre(Genre.valueOf(favGenre)); //Das ist der Name des Genres!, da dieser unique ist, suchen wir in den DB über den namen!
        }

        // Listen leer initialisieren
        user.setFavorites(new ArrayList<>());
        user.setRatings(new ArrayList<>());
        user.setRecommendations(new ArrayList<>());

        return user;
    }


    public Rating mapToRating(ResultSet ratingRs) {
        try {
            Rating rating = new Rating();

            rating.setRatingId(ratingRs.getObject("rating_id", UUID.class));
            rating.setCreatorId(ratingRs.getObject("creator_id", UUID.class));
            rating.setStars(ratingRs.getInt("stars"));
            rating.setConfirmed(ratingRs.getBoolean("confirmed"));
            rating.setMediaId(ratingRs.getObject("media_id", UUID.class));

            // Kommentar nur setzen, wenn confirmed == true? nein das gehört in den Service, hätte ich gesagt
            rating.setComment(ratingRs.getString("comment"));


            // likedBy muss auch später selbst genauer befüllt werden, wenn gebraucht
            rating.setLikedByList(new ArrayList<>());

            return rating;

        } catch (SQLException e) {
            throw new RuntimeException("Could not map ResultSet to Rating", e);
        }
    }

}
