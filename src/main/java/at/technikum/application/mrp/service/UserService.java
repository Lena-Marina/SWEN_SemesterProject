package at.technikum.application.mrp.service;

/*
Services kann ich auch weiter differenzieren z.B. einen eigenen Auth-Service
*/

import at.technikum.application.mrp.exception.EntityAlreadyExists;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserCredentials credentials) {
        /*
        - UserCreate (DTO) wird in ein richtiges User Objekt umgebaut + Validation (sind Daten leer?)
        - weitergabe des User objektes an die save Methode des Repositorys
        - ID setzen.
        */

        //Validation der Daten
        if(credentials.getUsername() == null || credentials.getUsername().isEmpty() ||
           credentials.getPassword() == null || credentials.getPassword().isEmpty()){
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        //existiert User bereits?
        Boolean userExists = this.userRepository.doesUserExist(credentials.getUsername());
        if(userExists){
            throw new EntityAlreadyExists("This Username is already taken!");
        }

        //weitere mögliche Validationen:
            // - wenn wir eine Vorgabe für den Aufbau des Username oder Passwort hätten - diese vor die Abfrage ob User bereits existiert geben, um eventuell Datenbankabfragen zu verringern


        //richtiger UserObjekt (nicht DTO) erstellen
        User newUser = new User();
        newUser.setUsername(credentials.getUsername());
        newUser.setPassword(credentials.getPassword());

        //ID erstellen
        newUser.setId(UUID.randomUUID()); //wir glauben wegen der geringen Wahrscheinlichkeit einer Koallision einfach, dass die UUID nicht bereits in der Datenbank vorkommt

        User safedUser = userRepository.save(newUser);

        //Rückweg
        if(safedUser.getUsername() == null || safedUser.getUsername().isEmpty()
        || safedUser.getPassword() == null || safedUser.getPassword().isEmpty()
        || safedUser.getId() == null){
            throw new EntityNotFoundException("User was not saved properly");
        }
        //am Rückweg soll der User kein Passwort mehr haben -> Passwort leeren
        //ich muss ein neues User - Objekt erstellen, dem ich das Passwort nicht gebe,
        //weil wenn ich das Passwort von safedUser = null setze, ändert es sich auch das im Repo gespeicherte Objekt, weil Referenz
        //sollte bei tatsächlicher DB-Anbindung nicht mehr notwendig sein!
        User responseUser = new User();
        responseUser.setId(safedUser.getId());
        responseUser.setUsername(safedUser.getUsername());
        responseUser.setEmail(safedUser.getEmail());
        responseUser.setFavoriteGenre(safedUser.getFavoriteGenre());

        return responseUser;
    }

    public List<User> getMostAktive() {
        List<User> mostAktiveUsers = userRepository.getMostAktive();
        return mostAktiveUsers;
    }
}
