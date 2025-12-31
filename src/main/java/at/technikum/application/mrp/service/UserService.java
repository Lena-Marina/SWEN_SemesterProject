package at.technikum.application.mrp.service;

/*
Services kann ich auch weiter differenzieren z.B. einen eigenen Auth-Service
*/

import at.technikum.application.mrp.exception.EntityAlreadyExistsException;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.model.dto.UserUpdate;
import at.technikum.application.mrp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserCredentials credentials) {
        /*
        - UserCreate (DTO) wird in ein richtiges User Objekt umgebaut
            + Validation (sind Daten leer? Ansosnten haben wir keine Vorgaben wie Username und Passwort aufgebaut sein müssen)
        - ID erstellen.
        - weitergabe des User objektes an die save Methode des Repositorys
        - zurückerhalten eines Optionals
            + Validation dessen bevor das gespeicherte User-Objekt zurückgegeben wird

        */

        //Validation der Daten
        if(credentials.getUsername() == null || credentials.getUsername().isEmpty() ||
           credentials.getPassword() == null || credentials.getPassword().isEmpty()){
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        //existiert User bereits?
        Boolean userExists = this.userRepository.doesUserExist(credentials.getUsername());
        if(userExists){
            throw new EntityAlreadyExistsException("This Username is already taken!");
        }

        //weitere mögliche Validationen:
            // - wenn wir eine Vorgabe für den Aufbau des Username oder Passwort hätten - diese vor die Abfrage ob User bereits existiert geben, um eventuell Datenbankabfragen zu verringern


        //richtiger UserObjekt (nicht DTO) erstellen
        User newUser = new User();
        newUser.setUsername(credentials.getUsername());
        newUser.setPassword(credentials.getPassword()); //passwort hashen!

        //ID erstellen
        newUser.setId(UUID.randomUUID()); //wir glauben wegen der geringen Wahrscheinlichkeit einer Koallision einfach, dass die UUID nicht bereits in der Datenbank vorkommt

        //DEBUGGING
        System.out.println("---------------------------------");
        System.out.println("DEBUG in UserService::registerUser() ");
        System.out.println("DEBUG: username = " + newUser.getUsername());
        System.out.println("DEBUG: password = " + newUser.getPassword());
        System.out.println("DEBUG: user_id = " + newUser.getId());


        //Repo funktion aufrufen
        Optional<User> safedUser = userRepository.create(newUser);

        //Rückweg
        if(safedUser.isEmpty())
        {
            throw new EntityNotSavedCorrectlyException("Unable to save user");
        }

        //Optional "auspacken"
        User unpackedSafeduser = safedUser.get();

        if(unpackedSafeduser.getUsername() == null || unpackedSafeduser.getUsername().isEmpty()
        || unpackedSafeduser.getPassword() == null || unpackedSafeduser.getPassword().isEmpty()
        || unpackedSafeduser.getId() == null){
            throw new EntityNotFoundException("User was not saved properly");
        }

        //am Rückweg soll der User kein Passwort mehr haben -> Passwort leeren bevor er zurück gegeben wird
        unpackedSafeduser.setPassword(null);

        //DEBUGGING
        System.out.println("---------------------------------");
        System.out.println("DEBUG zurückgegebener User in UserService::registerUser() ");
        System.out.println("DEBUG: username = " + newUser.getUsername());
        System.out.println("DEBUG: password = " + newUser.getPassword());
        System.out.println("DEBUG: user_id = " + newUser.getId());

        return unpackedSafeduser;
    }

    public List<User> getMostAktive() {
        List<User> mostAktiveUsers = userRepository.getMostAktive();
        return mostAktiveUsers;
    }

    public User getUserByID(String userID) {
         //id validierung

        Optional<User> optionalUser = this.userRepository.find(UUID.fromString("03fa85f6-4571-4562-b3fc-2c963f66afa6"));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get(); // das Optional umgibt den User, ich muss ihn/sie erst befreien

            /*paswort leeren, aber weil user eine Referenz auf das im Repos gespeicherte Objekt ist,
            * muss ich neues Objekt erstellen, damit das User-Objekt im Repo noch ein Passwort hat!
            * Ändern sobals echte DB anbindung da ist*/
            User responseUser = new User();
            responseUser.setId(user.getId());
            responseUser.setUsername(user.getUsername());
            responseUser.setEmail(user.getEmail());
            responseUser.setFavoriteGenre(user.getFavoriteGenre());

            return responseUser;
        }
        else{
            throw new EntityNotFoundException("User with id " + userID + " was not found");
        }


    }

    public User updateUser(UserUpdate update) {
        //DTO validieren.

        User updatedUser = /*repo methode aufrufen*/new User();
        updatedUser.setUsername("Data vanUpington");
        updatedUser.setEmail(update.getEmail());
        updatedUser.setFavoriteGenre(Genre.fromString(update.getFavoriteGenre()));

        //updatedUser valideren

        return updatedUser;
    }

    public /*List<Rating>*/void getUsersRatings(String userID) {
        //id validieren

        //repo Funktion aufrufen

        //erhaltene Liste validieren

        //validierte Liste zurückgeben.
    }
}
