package com.example.drawling.integration.api;

import com.example.drawling.application.controller.UserController;
import com.example.drawling.business.interfaces.service.UserService;
import com.example.drawling.domain.dto.UserDTO;
import com.example.drawling.domain.model.User;
import com.example.drawling.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user = new User(1, "testUser", "test@example.com", "password", "testUsername");
        UserDTO userDTO = new UserDTO();

        when(userService.getAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(ResponseEntity.ok(List.of(userDTO)), response);
        verify(userService, times(1)).getAll();
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetUserByName() {
        String name = "testUser";
        User user = new User(1, name, "test@example.com", "password", "testUsername");
        UserDTO userDTO = new UserDTO(); // Create a UserDTO object with necessary properties

        when(userService.getByDisplayName(name)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUserByName(name);

        assertEquals(ResponseEntity.ok(userDTO), response);
        verify(userService, times(1)).getByDisplayName(name);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetUserByNameNotFound() {
        String name = "nonExistentUser";

        when(userService.getByDisplayName(name)).thenReturn(null);

        ResponseEntity<UserDTO> response = userController.getUserByName(name);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(userService, times(1)).getByDisplayName(name);
    }

    @Test
    void testGetUserByUsername() {
        String username = "testUsername";
        User user = new User(1, "testUser", "test@example.com", "password", username);
        UserDTO userDTO = new UserDTO(); // Create a UserDTO object with necessary properties

        when(userService.getByUsername(username)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUserByUsername(username);

        assertEquals(ResponseEntity.ok(userDTO), response);
        verify(userService, times(1)).getByUsername(username);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetUserByUsernameNotFound() {
        String username = "nonExistentUsername";

        when(userService.getByUsername(username)).thenReturn(null);

        ResponseEntity<UserDTO> response = userController.getUserByUsername(username);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(userService, times(1)).getByUsername(username);
    }

    @Test
    void testGetUserById() {
        int id = 1;
        User user = new User(id, "testUser", "test@example.com", "password", "testUsername");

        when(userService.getById(id)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(id);

        assertEquals(ResponseEntity.ok(user), response);
        verify(userService, times(1)).getById(id);
    }

    @Test
    void testGetUserByIdNotFound() {
        int id = 1;

        when(userService.getById(id)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(id);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(userService, times(1)).getById(id);
    }
    @Test
    void testAddUser() {
        User user = new User(1, "testUser", "test@example.com", "password", "testUsername");
        User savedUser = new User(1, "testUser", "test@example.com", "password", "testUsername");

        when(userService.save(user)).thenReturn(savedUser);

        ResponseEntity<User> response = userController.addUser(user);

        assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(savedUser), response);
        verify(userService, times(1)).save(user);
    }

    @Test
    void testAddUserInternalServerError() {
        User user = new User(1, "testUser", "test@example.com", "password", "testUsername");

        when(userService.save(user)).thenReturn(null);

        ResponseEntity<User> response = userController.addUser(user);

        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(), response);
        verify(userService, times(1)).save(user);
    }

    @Test
    void testAddUserException() {
        User user = new User(1, "testUser", "test@example.com", "password", "testUsername");

        when(userService.save(user)).thenThrow(new RuntimeException("Error saving user"));

        ResponseEntity<User> response = userController.addUser(user);

        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(), response);
        verify(userService, times(1)).save(user);
    }

    @Test
    void testGetDisplayNameByUserId() {
        int id = 1;
        String displayName = "testUser";

        when(userService.getDisplayNameById(id)).thenReturn(displayName);

        ResponseEntity<String> response = userController.getDisplayNameByUserId(id);

        assertEquals(ResponseEntity.ok(displayName), response);
        verify(userService, times(1)).getDisplayNameById(id);
    }
}
