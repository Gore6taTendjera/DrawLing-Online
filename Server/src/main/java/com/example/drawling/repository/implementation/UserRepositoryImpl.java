package com.example.drawling.repository.implementation;

import com.example.drawling.business.interfaces.repository.UserRepository;
import com.example.drawling.domain.entity.UserEntity;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.UserDisplayNameNotFoundException;
import com.example.drawling.exception.UserNotFoundException;
import com.example.drawling.exception.UserRetrievalException;
import com.example.drawling.exception.UserSaveFailedException;
import com.example.drawling.mapper.UserMapper;
import com.example.drawling.repository.jpa.UserRepositoryJPA;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserRepositoryJPA jpa;
    private final UserMapper mapper;

    public UserRepositoryImpl(UserRepositoryJPA jpa, UserMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Transactional
    public User save(User user) {
        try{
            UserEntity savedUserEntity = jpa.save(mapper.toEntity(user));
            return jpa.findById(savedUserEntity.getId())
                    .map(mapper::toModel)
                    .orElseThrow(() -> new UserNotFoundException("User not found after saving with id: " + savedUserEntity.getId()));
        } catch (DataAccessException e) {
            throw new UserSaveFailedException("Failed to save the user.", e);
        }
    }


    @Transactional(readOnly = true)
    public User getByUsernameAndPassword(String username, String password) {
        try {
            return jpa.getByUsernameAndPassword(username, password)
                    .map(mapper::toModel)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        } catch (DataAccessException e) {
            throw new UserRetrievalException("Failed to retrieve user with username and password.", e);
        }
    }


    @Transactional(readOnly = true)
    public List<User> getAll() {
        try {
            List<UserEntity> userEntities = (List<UserEntity>) jpa.findAll();
            return userEntities.stream()
                    .map(mapper::toModel)
                    .toList();
        } catch (DataAccessException e) {
            throw new UserRetrievalException("Failed to retrieve all users.", e);
        }
    }

    @Transactional(readOnly = true)
    public int getTotalCount() {
        try {
            return jpa.getTotalCount();
        } catch (DataAccessException e) {
            throw new UserRetrievalException("Failed to retrieve total count of users.", e);
        }
    }

    @Transactional(readOnly = true)
    public User getById(int id) {
        try {
            return jpa.findById(id)
                    .map(mapper::toModel)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        } catch (DataAccessException e) {
            throw new UserRetrievalException("Failed to retrieve user with id: " + id, e);
        }
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        try {
            return jpa.findByUsername(username)
                    .map(mapper::toModel)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        } catch (DataAccessException e) {
            throw new UserRetrievalException("Failed to retrieve user with username: " + username, e);
        }
    }

    @Transactional(readOnly = true)
    public User getByDisplayName(String displayName) {
        try {
            return jpa.findByDisplayName(displayName)
                    .map(mapper::toModel)
                    .orElseThrow(() -> new UserNotFoundException("User not found with display name: " + displayName));
        } catch (DataAccessException e) {
            throw new UserRetrievalException("Failed to retrieve user with display name: " + displayName, e);
        }
    }

    @Transactional(readOnly = true)
    public String getDisplayNameById(int userId) {
        try {
            return jpa.findDisplayNameById(userId)
                    .orElseThrow(() -> new UserDisplayNameNotFoundException("Display name not found with id: " + userId));
        } catch (DataAccessException e) {
            throw new UserRetrievalException("Failed to retrieve display name with id: " + userId, e);
        }
    }

    @Transactional
    public int updateDisplayName(int userId, String displayName) {
        try {
            int result = jpa.setDisplayName(userId, displayName);
            if (result == 0) {
                throw new UserDisplayNameNotFoundException("Display name not found with id: " + userId);
            }
            return result;
        } catch (DataAccessException e) {
            throw new UserSaveFailedException("Failed to update display name for user id: " + userId, e);
        }
    }

}
