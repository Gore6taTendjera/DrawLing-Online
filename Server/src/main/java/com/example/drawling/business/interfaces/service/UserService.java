package com.example.drawling.business.interfaces.service;

import com.example.drawling.domain.model.User;

import java.util.List;

public interface UserService {

    User save(User user);

    User getById(int id);
    List<User> getAll();
    int getTotalCount();
    User getByDisplayName(String displayName);
    User getByUsername(String username);
    String getDisplayNameById(int id);

    int updateDisplayName(int id, String newDisplayName);

}
