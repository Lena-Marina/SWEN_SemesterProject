package at.technikum.application.mrp.service;


import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.model.Token;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.repository.UserRepository;

import java.util.Optional;

public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Token getToken(UserCredentials credentials)
    {
        //Validation der Daten
        if(credentials.getUsername() == null || credentials.getUsername().isEmpty() ||
                credentials.getPassword() == null || credentials.getPassword().isEmpty()){
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        Optional<User> foundUser = userRepository.findByCredentials(credentials.getUsername(), credentials.getPassword());

        if (foundUser.isPresent()) {
            User user = foundUser.get(); // das Optional umgibt den User, ich muss ihn/sie erst befreien
            return new Token(user.getUsername() + "-mrpToken");
        }
        else{
            throw new EntityNotFoundException("username or password is invalid");
        }
    }

}
