package at.technikum.application.mrp.service;

import at.technikum.application.common.Service;
import at.technikum.application.mrp.model.User;

import java.util.List;

public class AuthService implements Service<User> {
    @Override
    public void create(User object) {

    }

    @Override
    public User get(User object) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public User update(User object) {
        return null;
    }

    @Override
    public User delete(User object) {
        return null;
    }
}
