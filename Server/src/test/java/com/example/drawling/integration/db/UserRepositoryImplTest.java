package com.example.drawling.integration.db;

import com.example.drawling.domain.entity.UserEntity;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.UserDisplayNameNotFoundException;
import com.example.drawling.exception.UserNotFoundException;
import com.example.drawling.exception.UserSaveFailedException;
import com.example.drawling.mapper.UserMapper;
import com.example.drawling.repository.implementation.UserRepositoryImpl;
import com.example.drawling.repository.jpa.UserRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private UserRepositoryJPA jpa;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = new User(1, "displayName", "email@example.com", "password", "username");
        userEntity = new UserEntity(1, "displayName", "email@example.com", "password", "username");
    }

    @Test
    void save_ShouldReturnUser_WhenUserIsSavedSuccessfully() {
        when(mapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(jpa.save(any(UserEntity.class))).thenReturn(userEntity);
        when(jpa.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(mapper.toModel(any(UserEntity.class))).thenReturn(user);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser);
        assertEquals(user.getId(), savedUser.getId());
        verify(jpa, times(1)).save(any(UserEntity.class));
    }

    @Test
    void save_ShouldThrowUserSaveFailedException_WhenDataAccessExceptionOccurs() {
        when(mapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(jpa.save(any(UserEntity.class))).thenThrow(new DataAccessException("Error") {});

        assertThrows(UserSaveFailedException.class, () -> userRepository.save(user));
    }

    @Test
    void getByUsernameAndPassword_ShouldReturnUser_WhenUserExists() {
        when(jpa.getByUsernameAndPassword("username", "password")).thenReturn(Optional.of(userEntity));
        when(mapper.toModel(any(UserEntity.class))).thenReturn(user);

        User foundUser = userRepository.getByUsernameAndPassword("username", "password");

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void getByUsernameAndPassword_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(jpa.getByUsernameAndPassword("username", "password")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userRepository.getByUsernameAndPassword("username", "password"));
    }

    @Test
    void getAll_ShouldReturnListOfUsers_WhenUsersExist() {
        when(jpa.findAll()).thenReturn(List.of(userEntity));
        when(mapper.toModel(any(UserEntity.class))).thenReturn(user);

        List<User> users = userRepository.getAll();

        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        when(jpa.findById(1)).thenReturn(Optional.of(userEntity));
        when(mapper.toModel(any(UserEntity.class))).thenReturn(user);

        User foundUser = userRepository.getById(1);

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void getById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(jpa.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userRepository.getById(1));
    }

    @Test
    void updateDisplayName_ShouldReturnUpdatedCount_WhenUpdateIsSuccessful() {
        when(jpa.setDisplayName(1, "newDisplayName")).thenReturn(1);

        int result = userRepository.updateDisplayName(1, "newDisplayName");

        assertEquals(1, result);
    }

    @Test
    void updateDisplayName_ShouldThrowUserDisplayNameNotFoundException_WhenNoUserFound() {
        when(jpa.setDisplayName(1, "newDisplayName")).thenReturn(0);

        assertThrows(UserDisplayNameNotFoundException.class, () -> userRepository.updateDisplayName(1, "newDisplayName"));
    }

    @Test
    void getByUsername_ShouldReturnUser_WhenUserExists() {
        when(jpa.findByUsername("username")).thenReturn(Optional.of(userEntity));
        when(mapper.toModel(any(UserEntity.class))).thenReturn(user);

        User foundUser = userRepository.getByUsername("username");

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void getByUsername_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(jpa.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userRepository.getByUsername("username"));
    }

    @Test
    void getByDisplayName_ShouldReturnUser_WhenUserExists() {
        when(jpa.findByDisplayName("displayName")).thenReturn(Optional.of(userEntity));
        when(mapper.toModel(any(UserEntity.class))).thenReturn(user);

        User foundUser = userRepository.getByDisplayName("displayName");

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void getByDisplayName_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(jpa.findByDisplayName("displayName")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userRepository.getByDisplayName("displayName"));
    }

    @Test
    void getDisplayNameById_ShouldReturnDisplayName_WhenUserExists() {
        when(jpa.findDisplayNameById(1)).thenReturn(Optional.of("displayName"));

        String displayName = userRepository.getDisplayNameById(1);

        assertEquals("displayName", displayName);
    }

    @Test
    void getDisplayNameById_ShouldThrowUserDisplayNameNotFoundException_WhenUserDoesNotExist() {
        when(jpa.findDisplayNameById(1)).thenReturn(Optional.empty());

        assertThrows(UserDisplayNameNotFoundException.class, () -> userRepository.getDisplayNameById(1));
    }
}
