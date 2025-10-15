package at.technikum.application.mrp.service;

/*Anmerkung von Lektor:
eventuell an die Services fertige Objekte übergeben und nicht alle Parameter einzeln.
in den Services die Namen eher grob halten. -> Das hast du halt einfach verwechselt du Nudel
Services kann ich auch weiter differenzieren z.B. einen eigenen Auth-Service
*/

import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.UserCreate;
import at.technikum.application.mrp.repository.UserRepository;

import java.util.UUID;

public class UserService {

    private UserRepository userRepository;

    UserService(/*UserRepository userRepository*/) {
        this.userRepository = new UserRepository();
    }

    public void registerUser(UserCreate dto){
        /*
        - UserCreate (DTO) wird in ein richtiges User Objekt umgebaut + Validation (sind Daten leer?)
        - weitergabe des User objektes an die save Methode des Repositorys
        - ID setzen.
        */

        //Validation der Daten
        if(dto.getUsername() == null || dto.getUsername().isEmpty() ||
           dto.getPassword() == null || dto.getPassword().isEmpty()){
            throw new IllegalArgumentException("Username and password cannot be empty");
        }
            //weitere mögliche Validationen:
            // - wenn wir eine Vorgabe für den Aufbau des Username oder Passwort hätten
            // - wenn der Username einzigartig sein müsste (-> Abfrage an Datenbank ob bereits existiert

        //richtiger UserObjekt erstellen
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(dto.getPassword());

        //ID erstellen
        newUser.setId(UUID.randomUUID()); //wir glauben wegen der geringen Wahrscheinlichkeit einer Koallision einfach, dass die UUID nicht bereits in der Datenbank vorkommt

        userRepository.save(newUser);

        //Rückweg

    }
}
