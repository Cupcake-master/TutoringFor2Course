package ru.bulat.service.impl;

import ru.bulat.model.User;
import ru.bulat.repository.DatabaseConnection;
import ru.bulat.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    @Override
    public User save(User user) {
        return DatabaseConnection.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return DatabaseConnection.findByEmail(email);
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        DatabaseConnection.updatePassword(email, newPassword);
    }

    @Override
    public void delete(String email) {

    }
}
