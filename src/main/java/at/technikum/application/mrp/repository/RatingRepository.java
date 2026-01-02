package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.User;

import java.sql.Connection;
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
        //DB abfrage
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            // ? befüllen
            stmt.setObject(1,id);

            // Statement ausführen
            ResultSet rs = stmt.executeQuery();

           // Sichergehen, dass es ein Rating mit der ID gibt, bevor versucht wird es zu mappen
            if (!rs.next()) {
                return Optional.empty();
            }

            //Optional von Rating zurückgeben
            return Optional.of(mapToRating(rs));


        }catch(SQLException e){
            throw new EntityNotFoundException("Rating not found" + e.getMessage());
        }

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

    public void confirm(UUID ratingID)
    {
        String sql = "UPDATE ratings SET confirmed = ? WHERE rating_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql)){
            // ? Befüllen
            stmt.setBoolean(1, true); //brauche hier auch ein ? nicht um SQL Injektion zu verhindern, sondern um sicher zu gehen, dass ein Boolean gespeichert wird
            stmt.setObject(2, ratingID);

            // statement ausführen
            stmt.executeUpdate();
        }
        catch(SQLException e)
        {
            throw new EntityNotSavedCorrectlyException("Could not confirm Comment: " + e.getMessage());
        }
    }

    public UUID likeRating(UUID ratingID, UUID userID)
    {
        String sql = "INSERT INTO liked_by (rating_id, user_id) VALUES (?, ?)";

        try (PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql)) {

            stmt.setObject(1, ratingID);
            stmt.setObject(2, userID);

            int affected = stmt.executeUpdate(); //Wirft eine Exception, wenn nicht erfolgreich eingetragen
            if (affected != 1) {
                throw new EntityNotSavedCorrectlyException("Like was not saved");
            }

            return ratingID;

        } catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException("Could not like Rating: " + e.getMessage());
        }
    }


    private  Rating mapToRating(ResultSet rs) throws SQLException
    {
        Rating rating = new Rating();

        rating.setId(rs.getObject("rating_id", UUID.class));
        rating.setComment(rs.getString("comment"));
        rating.setStars(rs.getInt("stars"));
        rating.setConfirmed(rs.getBoolean("confirmed"));
        rating.setCreatorId(rs.getObject("creator_id", UUID.class));

        //Media aus DB holen und auch setzen?

        return rating;
    }

    public boolean likedBy(UUID ratingID, UUID userID)
    {
        String sql = "SELECT * FROM liked_by WHERE user_id=? AND rating_id=?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            // ? befüllen
            stmt.setObject(1,userID);
            stmt.setObject(2,ratingID);

            // Statement ausführen
            ResultSet rs = stmt.executeQuery();

            // Resultat verwalten
            if (!rs.next()) {
                return false;
            }
            return true;

        }catch(SQLException e)
        {   // für mich ist es zum Debuggen jetzt praktisch, so genau zu schreiben wo der Fehler liegt,
            // aber in der realität wäre es wsl schlecht den Namen der Tabelle in einer Exception zurück zu geben
            // da so SQL Injektion einfacher wird?
            throw new EntityNotFoundException("could not find row in liked_by" + e.getMessage());
        }
    }
}
