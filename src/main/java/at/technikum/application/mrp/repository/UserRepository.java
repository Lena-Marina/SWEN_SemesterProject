package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository implements MrpRepository<User>{

    private final ConnectionPool connectionPool;

    public UserRepository(ConnectionPool connectionPool) {
        //Register 1 oder 2 User zum Testen.
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<User> find(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try(PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql))
        {
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return Optional.empty(); // User existiert nicht
            }
            //Hier sollte ich auch noch die Listen befüllen, wenn ich den User gefunden habe, oder? -> kommt darauf an wofür ich ihn suche i guess
            return Optional.of(mapToUser(rs));
        } catch (Exception e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public Optional<User> findByCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return Optional.empty(); // User existiert nicht
            }
            //Hier sollte ich auch noch die Listen befüllen, wenn ich den User gefunden habe, oder? -> kommt darauf an wofür ich ihn suche i guess
            return Optional.of(mapToUser(rs));
        }
        catch (SQLException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public boolean doesUserExist(String username) {
        for (User user : users) {
            if(user.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> create(User object) {
        /*
        - speicherung des User-Objektes in Datenbank
        - Rückgabe des gespeicherten Users an den Service.
         */

        //User zur Datenbank hinzufügen
        // - dabei SQL Injektion mittels Prepared Statement (Vorgehen siehe Folien 'CRUD Operations') verhindern

        String sql = "INSERT INTO users (username, hashed_pw) VALUES (?, ?)"; //all die Dinge die ich nicht mitgebe, z.B. E-Mail, Favorite Genre usw. werden in der DB NULL oder auf ihre Default-Werte gesetzt

        try(PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql)){
            stmt.setString(1, object.getUsername());
            stmt.setString(2, object.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException(e.getMessage());
        }

        // hinzugefügten User zurückgeben -> ihn aus der DB holen | username hat eine eine unique-constraint
        sql = "SELECT * FROM users WHERE username = ?";
        try(PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, object.getUsername());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return Optional.empty(); // User existiert nicht
            }
            return Optional.of(mapToUser(rs));
        }
        catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException(e.getMessage());
        }

    }



    @Override
    public User update(User object) {
        return null;
    }

    @Override
    public User delete(UUID id) {
        return null;
    }

    public List<User> getMostAktive() {
        //Momentan retourniere ich einfach alle die es gibt -> ich weiß ja noch nichtmal was "most aktive" bedeutet
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try(PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql))
        {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                users.add(mapToUser(rs));
            }
        }
        catch (SQLException e) {
            throw new EntityNotFoundException(e.getMessage());
        }

        return users;
    }



    private User mapToUser(ResultSet rs) throws SQLException{
        User user = new User();

        String uuidString = rs.getString("user_id");
        if (uuidString != null) {
            user.setId(UUID.fromString(uuidString));
        }

        // einfache Strings
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("hashed_pw"));
        user.setEmail(rs.getString("email"));

        //fav Genre ist ein Enum, daher der extra Wrap
        user.setFavoriteGenre(Genre.valueOf(rs.getString("fav_genre")));

        // Listen leer initialisieren
        user.setFavorites(new ArrayList<>());
        user.setRatings(new ArrayList<>());
        user.setRecommendations(new ArrayList<>());

        return user;
    }
}
