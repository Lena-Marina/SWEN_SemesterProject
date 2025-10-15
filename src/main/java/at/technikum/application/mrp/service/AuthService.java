package at.technikum.application.mrp.service;


import at.technikum.application.mrp.repository.UserRepository;

public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
