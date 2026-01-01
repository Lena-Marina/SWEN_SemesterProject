package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class FavoriteRepository {
    private final ConnectionPool connectionPool;

    public FavoriteRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
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
            throw new EntityNotSavedCorrectlyException("could not mark as favorite!" + e.getMessage());
        }

    }

    public void unmarkAsFavorite(UUID mediaID, UUID userID){

    }

    //public ? getUserFavorites

}
