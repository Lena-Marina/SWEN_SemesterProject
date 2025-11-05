package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository implements MrpRepository<User>{

    private final ConnectionPool connectionPool;

    //Als Simulation unserer Datenbank eine Liste an Usern.
    private List<User> users;

    public UserRepository(ConnectionPool connectionPool) {
        //Register 1 oder 2 User zum Testen.
        this.connectionPool = connectionPool;
        this.users = new ArrayList<>();
        this.users.add(new User("Maximilia", "Passwort1234", "maximilia.mustermann@email.com", Genre.HORROR, UUID.fromString("03fa85f6-4571-4562-b3fc-2c963f66afa6")));
    }

    @Override
    public Optional<User> find(String id) {
        for (User user : users) {
            if(user.getId().equals(UUID.fromString(id))){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> findByCredentials(String username, String password) {
        for (User user : users) {
            System.out.println("Comparing saved: '" + user.getUsername() + "' / '" + user.getPassword() + "'" +
                    " with input: '" + username + "' / '" + password + "'");
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
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
    public User save(User object) {
        /*
        - speicherung des User-Objektes in Datenbank (momentan
        - Rückgabe des gespeicherten Users an den Service.
         */

        //nicht bekannt Werte NULL setzen, damit in der Datenbank klar ist, dass sie unbekannt und nicht leer sind!
        //Bsp: der User hat nicht kein favourite Genre, sondern wir wissen nicht, ob sie eines hat
        if (object.getEmail() != null && object.getEmail().isEmpty()) {
            object.setEmail(null);
        }
        if (object.getFavoriteGenre() != null) {
            object.setFavoriteGenre(null);
        }
        if(object.getFavorites() != null && object.getFavorites().isEmpty()){
            object.setFavorites(null);
        }
        if(object.getRatings() != null && object.getRatings().isEmpty()){
            object.setRatings(null);
        }
        if(object.getRecommendations() != null && object.getRecommendations().isEmpty()){
            object.setRecommendations(null);
        }

        //User zur Datenbank hinzufügen
        users.add(object);

        //Debugging
        User last = users.get(users.size() - 1);
        System.out.println("Saved username: '" + last.getUsername() + "'");
        System.out.println("Saved password: '" + last.getPassword() + "'");
        System.out.println(users.get(users.size()-1).toString());

        //hinzugefügten User zurückgeben.
        return users.get(users.size()-1);
    }

    @Override
    public User update(User object) {
        return null;
    }

    @Override
    public User delete(String id) {
        return null;
    }

    public List<User> getMostAktive() {
        //Momentan retourniere ich einfach alle die es gibt -> ich weiß ja noch nichtmal was "most aktive" bedeutet
        return users;
    }
}
