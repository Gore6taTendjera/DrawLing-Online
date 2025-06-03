package com.example.drawling.business.implementation;

import com.example.drawling.business.interfaces.repository.UserRepository;
import com.example.drawling.business.interfaces.service.UserService;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.UserRetrievalException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ID_MUST_BE_GREATER_THAN_ZERO = "ID must be greater than zero.";

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(ID_MUST_BE_GREATER_THAN_ZERO);
        }
        return userRepository.getById(id);
    }

    public User getByDisplayName(String displayName) {
        if (displayName == null || displayName.isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be null or empty.");
        }
        return userRepository.getByDisplayName(displayName);
    }

    public User getByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        return userRepository.getByUsername(username);
    }

    public String getDisplayNameById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(ID_MUST_BE_GREATER_THAN_ZERO);
        }
        String displayName = userRepository.getDisplayNameById(id);
        if (displayName == null) {
            throw new UserRetrievalException("Display name not found for user id: " + id);
        }
        return displayName;

    }

    public int updateDisplayName(int id, String newDisplayName) {
        if (id <= 0) {
            throw new IllegalArgumentException(ID_MUST_BE_GREATER_THAN_ZERO);
        }
        if (newDisplayName == null || newDisplayName.isEmpty()) {
            throw new IllegalArgumentException("New display name cannot be null or empty.");
        }
        return userRepository.updateDisplayName(id, newDisplayName);

    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public int getTotalCount() {
        return userRepository.getTotalCount();
    }
}
