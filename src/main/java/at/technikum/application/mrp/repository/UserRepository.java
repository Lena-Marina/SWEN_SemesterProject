package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try(PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql))
        {
            stmt.setObject(1, id);
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
        String sql = "SELECT * FROM users WHERE username = ? AND hashed_pw = ?";
        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                //Sollte ich noch die Listen befüllen? -> muss ich nicht, da ich eigentlich nur prüfen muss ob Username und Passwort in der DB sind
                return Optional.of(mapToUser(rs));
            }
        }
        catch (SQLException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public boolean doesUserExist(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, username);

            try(ResultSet rs = stmt.executeQuery();)
            {
                if (!rs.next()) {
                    return false;
                }
                return true;
            }
        }
        catch (SQLException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
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

        String sql = "INSERT INTO users (username, hashed_pw, user_id) VALUES (?, ?, ?)"; //all die Dinge die ich nicht mitgebe, z.B. E-Mail, Favorite Genre usw. werden in der DB NULL oder auf ihre Default-Werte gesetzt

        try(PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql)){
            stmt.setString(1, object.getUsername());
            stmt.setString(2, object.getPassword());
            stmt.setObject(3, object.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new EntityNotSavedCorrectlyException(e.getMessage());
        }

        // hinzugefügten User zurückgeben -> ihn aus der DB holen | username hat eine unique-constraint
        sql = "SELECT * FROM users WHERE username = ?";
        try(PreparedStatement stmt = connectionPool.getConnection().prepareStatement(sql))
        {
            //DEBUGGING
            System.out.println("---------------------------------");
            System.out.println("DEBUG in UserRepository::create() ");
            System.out.println("DEBUG: username = " + object.getUsername());

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
    public Optional<User> update(User object) { /*Pfad: users/profile PUT*/
        //über die id
        String sql = "UPDATE users SET email = ?, fav_genre = ? WHERE user_id = ?";

        try(PreparedStatement stmt = this.connectionPool.getConnection().prepareStatement(sql))
        {
            stmt.setString(1, object.getEmail());

            if (object.getFavoriteGenre() != null) {
                stmt.setString(2, object.getFavoriteGenre().name());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setObject(3, object.getId());

            stmt.executeUpdate();

        }
        catch(SQLException e)
        {
            throw  new EntityNotSavedCorrectlyException(e.getMessage());
        }

        //geupdatete User*in zurückgeben:
        return find(object.getId());
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
}
