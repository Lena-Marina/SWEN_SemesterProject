package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RatingRepository implements MrpRepository<Rating>{
    private final ConnectionPool connectionPool;

    public RatingRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Rating> find(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Rating> findAll() {
        return List.of();
    }

    @Override
    public Optional<Rating> create(Rating object) {

        String sql = "INSERT INTO ratings (rating_id, creator_id, media_id, comment, stars, confirmed) VALUES (?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            // ? auffüllen
            stmt.setObject(1, object.getId());
            stmt.setObject(2, object.getCreatorID());
            stmt.setObject(3, object.getMedia().getId());
            stmt.setString(4, object.getComment());
            stmt.setInt(5, object.getStars());
            stmt.setBoolean(6, object.getConfirmed());
                //timestamp wird glaube ich automatisch von der DB erstellt!

            //statement ausführen
            stmt.executeUpdate();

        }catch(SQLException e)
        {
            throw new EntityNotSavedCorrectlyException("Could not save Rating: " + e.getMessage());
        }

        //Mittels neuer Anfrage Rating aus DB auslesen
        sql = "SELECT * FROM ratings WHERE rating_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql)){
            // ? Befüllen
            stmt.setObject(1, object.getId());

            // Statement ausführen
            ResultSet rs = stmt.executeQuery();

            //Statement verarbeiten -> Optional des daraus erhaltenen Ratings retournieren
            if (rs.next()) {
                Rating rating = new Rating();
                rating.setId(rs.getObject("rating_id", UUID.class));
                rating.setStars(rs.getInt("stars"));
                rating.setComment(rs.getString("comment"));
                rating.setConfirmed(rs.getBoolean("confirmed"));

                rating.setCreatorId(rs.getObject("creator_id", UUID.class));

                // Media-Referenz -> dafür muss ich wieder ein Media Objekt erstellen
                Media media = new Media();
                media.setId(rs.getObject("media_id", UUID.class));
                rating.setMedia(media);

                return Optional.of(rating);
            }
        }
        catch(SQLException e)
        {

            throw new EntityNotFoundException("Could not find saved Rating: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Rating> update(Rating object) {
        return Optional.empty();
    }

    @Override
    public Rating delete(UUID id) {
        return null;
    }
}
