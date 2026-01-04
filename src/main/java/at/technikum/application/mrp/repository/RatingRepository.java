package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.helper.RatingStatistic;
import at.technikum.application.mrp.model.helper.RecommendationHelper;
import at.technikum.application.mrp.model.util.ModelMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RatingRepository implements MrpRepository<Rating>{
    private final ConnectionPool connectionPool;
    private final ModelMapper mapper;

    public RatingRepository(ConnectionPool connectionPool,  ModelMapper mapper) {
        this.connectionPool = connectionPool;
        this.mapper = mapper;
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
            return Optional.of(mapper.mapToRating(rs));


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
            stmt.setObject(1, object.getRatingId());
            stmt.setObject(2, object.getCreatorID());
            stmt.setObject(3, object.getMediaID());
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
            stmt.setObject(1, object.getRatingId());

            // Statement ausführen
            ResultSet rs = stmt.executeQuery();

            //Statement verarbeiten -> Optional des daraus erhaltenen Ratings retournieren
            if (rs.next()) {
                Rating rating = new Rating();
                rating.setRatingId(rs.getObject("rating_id", UUID.class));
                rating.setStars(rs.getInt("stars"));
                rating.setComment(rs.getString("comment"));
                rating.setConfirmed(rs.getBoolean("confirmed"));

                rating.setCreatorId(rs.getObject("creator_id", UUID.class));
                rating.setMediaId(rs.getObject("media_id", UUID.class));

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
    public Optional<Rating> update(Rating object)
    {
        String updateSql =
                "UPDATE ratings SET stars = ?, comment = ?, confirmed = ? WHERE rating_id = ?";
        String selectSql =
                "SELECT * FROM ratings WHERE rating_id = ?";

        try (Connection con = connectionPool.getConnection()) {
            con.setAutoCommit(false); // Einheitliche Transaktion

            // Änderung Speichern
            try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
                updateStmt.setInt(1, object.getStars());
                updateStmt.setString(2, object.getComment());
                updateStmt.setBoolean(3, object.getConfirmed());
                updateStmt.setObject(4, object.getRatingId());

                int affected = updateStmt.executeUpdate();
                if (affected != 1) {
                    con.rollback();
                    return Optional.empty();
                }
            }

            // Änderung fürs Zurückgeben auslesen
            try (PreparedStatement selectStmt = con.prepareStatement(selectSql)) {
                selectStmt.setObject(1, object.getRatingId());

                ResultSet rs = selectStmt.executeQuery();
                if (!rs.next()) {
                    con.rollback();
                    return Optional.empty();
                }

                Rating rating = mapper.mapToRating(rs);
                con.commit(); // Transaktion beenden
                return Optional.of(rating);
            }

        } catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException(
                    "Could not update Rating: " + e.getMessage());
        }
    }


    @Override
    public Rating delete(UUID id)
    {
        String selectRatingSql =
                "SELECT rating_id, stars, comment, confirmed, creator_id, media_id " +
                        "FROM ratings WHERE rating_id = ?";

        String selectLikesSql =
                "SELECT user_id FROM liked_by WHERE rating_id = ?";

        String deleteLikesSql =
                "DELETE FROM liked_by WHERE rating_id = ?";

        String deleteRatingSql =
                "DELETE FROM ratings WHERE rating_id = ?";

        try (Connection con = connectionPool.getConnection()) {
            con.setAutoCommit(false); //Transaktion öffnen

            Rating deletedRating;
            UUID mediaId;

            // 1. Rating zum Zurückgeben Speichern
            try (PreparedStatement stmt = con.prepareStatement(selectRatingSql)) {
                stmt.setObject(1, id);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    con.rollback();
                    throw new EntityNotFoundException("Rating not found");
                }

                deletedRating = mapper.mapToRating(rs);
            }

            // 2. Einträge aus liked_by speichern
            try (PreparedStatement stmt = con.prepareStatement(selectLikesSql)) {
                stmt.setObject(1, id);
                ResultSet rs = stmt.executeQuery();

                List<UUID> likedBy = new ArrayList<>();
                while (rs.next()) {
                    likedBy.add(rs.getObject("user_id", UUID.class));
                }

                deletedRating.setLikedByList(likedBy);
            }

            // 3. Likes löschen
            try (PreparedStatement stmt = con.prepareStatement(deleteLikesSql)) {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            }

            // 4. Rating löschen
            try (PreparedStatement stmt = con.prepareStatement(deleteRatingSql)) {
                stmt.setObject(1, id);

                int affected = stmt.executeUpdate();
                if (affected != 1) {
                    con.rollback();
                    throw new EntityNotSavedCorrectlyException("Rating not deleted");
                }
            }

            con.commit(); // Transaktion beenden
            return deletedRating;

        } catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException(
                    "Could not delete Rating: " + e.getMessage());
        }
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

    public List<Rating> findAllFrom(UUID userID)
    {
        List<Rating> userRatings = new ArrayList<>();

        String selectRatingsSql = "SELECT * FROM ratings WHERE creator_id = ?";
        String selectLikesSql = "SELECT user_id FROM liked_by WHERE rating_id = ?";

        try (Connection con = connectionPool.getConnection();
             PreparedStatement ratingStmt = con.prepareStatement(selectRatingsSql);
             PreparedStatement likesStmt = con.prepareStatement(selectLikesSql);
             ) {

            // Zuerst Ratings laden
            ratingStmt.setObject(1, userID);
            ResultSet ratingRs = ratingStmt.executeQuery();

            //für jedes Ergebnis, die informationen über das Rating speichern
            while (ratingRs.next()) {
                //"basis" Felder des Ratings befüllen
                Rating rating = mapper.mapToRating(ratingRs);

                //liked_by liste laden und zum Rating dazugeben
                List<UUID> likedBy = new ArrayList<>();

                //? von likesStmt laden
                likesStmt.setObject(1, ratingRs.getObject("rating_id", UUID.class));
                ResultSet likesRs = likesStmt.executeQuery();
                while (likesRs.next()) {
                    likedBy.add(likesRs.getObject("user_id", UUID.class));
                }
                rating.setLikedByList(likedBy);
                userRatings.add(rating);
            }

            return userRatings;

        } catch (SQLException e) {
            throw new EntityNotFoundException(
                    "Could not load ratings for user: " + e.getMessage(), e);
        }
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

    public RecommendationHelper getTypeWithStars(UUID ratingId) {
        String sql = "SELECT me.type, r.stars " +
                "FROM ratings r " +
                "JOIN media_entry me ON me.media_id = r.media_id " +
                "WHERE r.rating_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            // ? befüllen
            stmt.setObject(1, ratingId);

            // stmt ausführen
            ResultSet rs = stmt.executeQuery();

            // Ergebnis auswerten
            if(!rs.next()) {
                throw new EntityNotFoundException("could not find row in ratings");
            }
            RecommendationHelper recom = new RecommendationHelper();
            recom.setStars(rs.getInt("stars"));
            recom.setName(rs.getString("type"));
            return recom;


        }catch(SQLException e)
        {
            throw new EntityNotFoundException("could not find rating with id " + ratingId + "in ratings AND/OR: " + e.getMessage());
        }

    }

    public List<RecommendationHelper> getGenresWithStars(UUID ratingId) {
        String sql = "SELECT g.name, r.stars " +
                "FROM ratings r " +
                "JOIN media_entry me ON me.media_id = r.media_id " +
                "JOIN is_genre ig ON me.media_id = ig.media_id " +
                "JOIN genre g ON ig.genre_id = g.genre_id " +
                "WHERE r.rating_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql)) {
            stmt.setObject(1, ratingId);
            ResultSet rs = stmt.executeQuery();

            List<RecommendationHelper> recoms = new ArrayList<>();
            while(rs.next()) {
                RecommendationHelper recom = new RecommendationHelper();
                recom.setStars(rs.getInt("stars"));
                recom.setName(rs.getString("name")); // entspricht dem Genre-Name
                recoms.add(recom);
            }
            return recoms;

        } catch(SQLException e) {
            throw new EntityNotFoundException("Could not get genres for rating " + ratingId + ": " + e.getMessage());
        }
    }

    public RatingStatistic getRatingStatistic(UUID mediaID) {
        String sql = "SELECT stars FROM ratings WHERE media_id=?";

        RatingStatistic rst = new RatingStatistic();
        rst.setNumberOfRatings(0);
        rst.setSumOfStars(0);

        try (PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql)) {
            stmt.setObject(1, mediaID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int stars = rs.getInt("stars");
                rst.setSumOfStars(rst.getSumOfStars() + stars);
                rst.setNumberOfRatings(rst.getNumberOfRatings() + 1);
            }

            if (rst.getNumberOfRatings() == 0) {
                throw new EntityNotFoundException("Could not find ratings for media id " + mediaID);
            }

        } catch (SQLException e) {
            throw new EntityNotFoundException("Problem in getRatingStatistic(): " + e.getMessage());
        }

        return rst;
    }

    public boolean alreadyRatedByUser(UUID mediaId, UUID userId) {
        String sql = "SELECT * from ratings WHERE media_id = ? AND creator_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql)){
            // ? befüllen
            stmt.setObject(1, mediaId);
            stmt.setObject(2, userId);

            // stmt ausführen
            ResultSet rs = stmt.executeQuery();

            //Ergebnis verarbeiten
            if(!rs.next()) {
                return false;
            }
            return true;

        }catch(SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
}
