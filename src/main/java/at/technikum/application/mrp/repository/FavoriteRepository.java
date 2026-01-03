package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EnityNotDeletedCorrrectlyException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.util.ModelMapper;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FavoriteRepository {
    private final ConnectionPool connectionPool;
    private final ModelMapper mapper;

    public FavoriteRepository(ConnectionPool connectionPool, ModelMapper mapper) {
        this.connectionPool = connectionPool;
        this.mapper = mapper;
    }

    public void markAsFavorite(UUID mediaID, UUID userID){
        String sql = "INSERT INTO favorites (media_id, user_id) VALUES (?, ?)";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            // ? befüllen
            stmt.setObject(1,mediaID);
            stmt.setObject(2,userID);

            // statement ausführen
            stmt.executeUpdate();

        }catch(SQLException e)
        {
            throw new EntityNotSavedCorrectlyException("could not mark as favorite: " + e.getMessage());
        }

    }

    public void unMarkAsFavorite(UUID mediaID, UUID userID){
        String sql = "DELETE FROM favorites WHERE media_id = ? AND user_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            // ? auffüllen
            stmt.setObject(1,mediaID);
            stmt.setObject(2,userID);

            // Statement ausführen
            stmt.executeUpdate();

        } catch( SQLException e)
        {
            throw new EnityNotDeletedCorrrectlyException("could not unmark as favorite: " + e.getMessage());
        }
    }

    public List<Media> findAllFrom(UUID userId)
    {
        List<Media> favourites = new ArrayList<>();

        String favSql = "SELECT media_id FROM favorites WHERE user_id = ?";
        String mediaSql = "SELECT * FROM media_entry WHERE media_id = ?";
        String genreSql =
                "SELECT g.* FROM is_genre ig " +
                        "JOIN genre g ON ig.genre_id = g.genre_id " +
                        "WHERE ig.media_id = ?";
        String ratingSql = "SELECT * FROM ratings WHERE media_id = ?";
        String likedBySql = "SELECT user_id FROM liked_by WHERE rating_id = ?";

        try (Connection con = connectionPool.getConnection();
             PreparedStatement favStmt = con.prepareStatement(favSql);
             PreparedStatement mediaStmt = con.prepareStatement(mediaSql);
             PreparedStatement genreStmt = con.prepareStatement(genreSql);
             PreparedStatement ratingStmt = con.prepareStatement(ratingSql);
             PreparedStatement likedByStmt = con.prepareStatement(likedBySql))
        {
            // 1.) alle Einträge zur user_id in favorites finden
            favStmt.setObject(1, userId);
            ResultSet favRs = favStmt.executeQuery();

            while (favRs.next()) {
                UUID mediaId = favRs.getObject("media_id", UUID.class);

                // 2.) zu allen gefundenen Einträgen aus favorites das passende Media in media_entry finden
                mediaStmt.setObject(1, mediaId);
                ResultSet mediaRs = mediaStmt.executeQuery();

                if (!mediaRs.next()) continue;

                Media media = mapper.mapToMedia(mediaRs);

                // 3.) Die liste genres in Media befüllen
                genreStmt.setObject(1, mediaId);
                ResultSet genreRs = genreStmt.executeQuery();

                List<Genre> genres = new ArrayList<>();
                while (genreRs.next()) {
                    String genreName = genreRs.getString("name");
                    genres.add(Genre.fromString(genreName));
                }
                media.setGenres(genres);

                // 4.) die Liste ratings in Media befüllen
                ratingStmt.setObject(1, mediaId);
                ResultSet ratingRs = ratingStmt.executeQuery();

                List<Rating> ratings = new ArrayList<>();
                while (ratingRs.next()) {
                    Rating rating = mapper.mapToRating(ratingRs);

                    // 5.) die liste liked_by aus dem rating speichern
                    likedByStmt.setObject(1, rating.getRatingId());
                    ResultSet likedRs = likedByStmt.executeQuery();

                    List<UUID> likedBy = new ArrayList<>();
                    while (likedRs.next()) {
                        likedBy.add(likedRs.getObject("user_id", UUID.class));
                    }

                    rating.setLikedByList(likedBy);

                    //6.) in Ratings gibt es das Media auf das es sich bezieht
                    rating.setMedia(media);

                    ratings.add(rating);
                }

                media.setRatings(ratings);
                favourites.add(media);
            }

            return favourites;

        } catch (SQLException e) {
            throw new EntityNotFoundException(
                    "Could not find all favorites: " + e.getMessage(), e);
        }
    }


}
