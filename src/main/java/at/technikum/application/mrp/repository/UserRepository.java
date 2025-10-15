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
        this.users.add(new User("03fa85f64-5717-4562-b3fc-2c963f66afa6",
                "MaximiliaMusterfrau",
                "$argon2i$v=19$m=16,t=2,p=1$eDRZeFNUaDFnRTlvYWpNbw$cM0CsGb0Gy049dQJ49AOlg", //= passwort1234
                "maximiliaMusterfrau@beispiel.com",
                "Thriller"
        ));
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
        //Wenn E-Mail und/oder Favortite Genre == "" muss man es in NULL übersetzen für die Datenbank
        return null;
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
