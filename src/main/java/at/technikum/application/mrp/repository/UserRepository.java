package at.technikum.application.mrp.repository;

import at.technikum.application.mrp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements MrpRepository<User>{

    //Als Simulation unserer Datenbank eine Liste an Usern.
    private List<User> users;

    public UserRepository() {
        //Register 1 oder 2 User zum Testen.
        this.users = new ArrayList<>();
        this.users.add(new User("Maximilia", "Passwort1234", "maximilia.mustermann@email.com", "Horror"));
    }

    @Override
    public Optional<User> find(String id) {
        for (User user : users) {
            if(user.getId().equals(id)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
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
        //Bsp: der User hat nicht kein favorutite Genre, sondern wir wissen nicht ob sie eines hat
        if (object.getEmail() != null && object.getEmail().isEmpty()) {
            object.setEmail(null);
        }
        if (object.getFavoriteGenre() != null && object.getFavoriteGenre().isEmpty()) {
            object.setFavoriteGenre(null);
        }
        if(object.getFavoriteGenre() != null && object.getFavoriteGenre().isEmpty()){
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
}
