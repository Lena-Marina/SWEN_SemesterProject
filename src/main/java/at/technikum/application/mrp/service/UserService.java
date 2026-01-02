package at.technikum.application.mrp.service;

/*
Services kann ich auch weiter differenzieren z.B. einen eigenen Auth-Service
*/

import at.technikum.application.mrp.exception.EntityAlreadyExistsException;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.InvalidEntityException;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.model.dto.UserUpdate;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private UserRepository userRepository;
    private RatingRepository ratingRepository;

    public UserService(UserRepository userRepository,
                       RatingRepository ratingRepository)
    {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
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
        UUID uuid;

        //id validierung -> Ist es überhaupt eine UUID?
        try {
            uuid = UUID.fromString(userID);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("UserID is not a valid UUID" + e.getMessage());
        }

        //Repo-Funktion aufrufen
        Optional<User> userOpt = this.userRepository.find(uuid);

        if (userOpt.isPresent()) {
            User user = userOpt.get(); // das Optional umgibt den User, ich muss ihn/sie erst befreien

            //Passwort nicht zurückgeben
            user.setPassword(null);

            //Sonstige validierung? username muss existieren! Rest darf eigentlich NULL sein
            if(user.getUsername() == null || user.getUsername().isEmpty()){
                throw new InvalidEntityException("Username is empty");
            }

            return user;
        }
        else{
            throw new EntityNotFoundException("User with id " + userID + " was not found");
        }
    }

    public User updateUser(UserUpdate update) {
        //DTO validieren.
            //darf fav Genre oder email empty sein?
                //ich denke ja, weil ich könnte ja nur eines updaten
                    //dann darf ich in der DB aber nur updaten, wenn dort nicht bereits etwas steht
                        //sonst würde ich einen Eintrag ja wieder mit NULL überschreiben!

        //Id darf jedenfalls nicht NULL sein!
        if(update.getUserID() == null){
            throw new IllegalArgumentException("UserID cannot be empty");
        }

        Optional<User> userOpt = this.userRepository.find(update.getUserID());

        if (userOpt.isEmpty()) { //der User muss existieren, um geupdated zu werden
            throw new EntityNotFoundException("User not found");
        }

        //User aus Optional entpacken
        User existingUser = userOpt.get(); //dieser wird, eventuell geupdated an die eigentliche Repo-Funktion dann weitergegeben

        //E-Mail wenn kein Update gekommen ist, aber bereits ein Eintrag existiert
        String email = update.getEmail();
        if (email == null || email.isBlank()) {
            email = existingUser.getEmail();
        }

        //fav Genre, wenn kein Update gekommen ist, aber bereits ein Eintrag existiert!
        Genre favoriteGenre = update.getFavoriteGenre();
        if (favoriteGenre == null) {
            favoriteGenre = existingUser.getFavoriteGenre(); //es kann sein, dass wieder null drüber gespielt wird, wenn das in der DB sowieso schon so war
        }

        existingUser.setEmail(email);
        existingUser.setFavoriteGenre(favoriteGenre);

        //Repo Methode aufrufen
        Optional<User> updatedUserOpt = this.userRepository.update(existingUser);

        //updatedUser valideren
        if(!updatedUserOpt.isPresent()){
            throw new EntityNotFoundException("User was not updated because they do not exist!");
        }

        User updatedUser = updatedUserOpt.get();

        if((updatedUser.getFavoriteGenre().equals(existingUser.getFavoriteGenre()))
            && (updatedUser.getEmail().equals(existingUser.getEmail()))
                && (updatedUser.getId().equals(existingUser.getId()))
        ){
            return updatedUser;
        }

        throw new EntityNotSavedCorrectlyException("User not safed correctly");
    }

    public List<Rating> getUserRatings(UUID userID) {
        //id validieren -> Muss nicht, hätte nicht in UUID übersetzt werden können, wenn es nicht UUID wäre

        //repo Funktion aufrufen
        List<Rating> ratings = this.ratingRepository.findAllFrom(userID);

        //erhaltene Liste validieren? auf was prüfen?
        //alle Kommentare von inhalten die nicht confirmed sind, auf "" setzen
        for (Rating rating : ratings) {
            if (!rating.getConfirmed()) {
                rating.setComment("");
            }
        }

        //validierte Liste zurückgeben.
        return ratings;
    }
}
