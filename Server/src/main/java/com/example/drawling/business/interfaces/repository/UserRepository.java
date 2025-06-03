package com.example.drawling.business.interfaces.repository;

import com.example.drawling.domain.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);
    List<User> getAll();
    int getTotalCount();
    User getById(int id);
    User getByUsername(String username);
    User getByDisplayName(String displayName);
    User getByUsernameAndPassword(String username, String password);

    String getDisplayNameById(int id);
    int updateDisplayName(int id, String displayName);
}
