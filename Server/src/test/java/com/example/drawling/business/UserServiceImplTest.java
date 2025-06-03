package com.example.drawling.business;

import com.example.drawling.business.implementation.UserServiceImpl;
import com.example.drawling.business.interfaces.repository.UserRepository;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.UserRetrievalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // mocks
    }

    @Test
    void save_validUser_shouldSaveUserSuccessfully() {
        User user = new User();
        user.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void save_nullUser_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.save(null));
    }

    @Test
    void getById_validId_shouldReturnUser() {
        User user = new User();
        when(userRepository.getById(1)).thenReturn(user);

        User result = userService.getById(1);

        assertNotNull(result);
        verify(userRepository).getById(1);
    }

    @Test
    void getById_invalidId_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.getById(0));
        assertThrows(IllegalArgumentException.class, () -> userService.getById(-1));
    }

    @Test
    void getByDisplayName_validDisplayName_shouldReturnUser() {
        User user = new User();
        when(userRepository.getByDisplayName("displayName")).thenReturn(user);

        User result = userService.getByDisplayName("displayName");

        assertNotNull(result);
        verify(userRepository).getByDisplayName("displayName");
    }


    @Test
    void getByUsername_validUsername_shouldReturnUser() {
        User user = new User();
        when(userRepository.getByUsername("username")).thenReturn(user);

        User result = userService.getByUsername("username");

        assertNotNull(result);
        verify(userRepository).getByUsername("username");
    }

    @Test
    void getDisplayNameById_validId_shouldReturnDisplayName() {
        when(userRepository.getDisplayNameById(1)).thenReturn("displayName");

        String displayName = userService.getDisplayNameById(1);

        assertEquals("displayName", displayName);
    }


    @Test
    void updateDisplayName_validId_shouldReturnUpdatedCount() {
        when(userRepository.updateDisplayName(1, "newDisplayName")).thenReturn(1);

        int result = userService.updateDisplayName(1, "newDisplayName");

        assertEquals(1, result);
        verify(userRepository).updateDisplayName(1, "newDisplayName");
    }



    @Test
    void getAll_shouldReturnUserList() {
        List<User> userList = new ArrayList<>();
        when(userRepository.getAll()).thenReturn(userList);

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(userList, result);
    }

    @Test
    void getByDisplayName_invalidDisplayName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.getByDisplayName(null));
        assertThrows(IllegalArgumentException.class, () -> userService.getByDisplayName(""));
    }


    @Test
    void getTotalCount_shouldReturnCount() {
        when(userRepository.getTotalCount()).thenReturn(100);

        int result = userService.getTotalCount();

        assertEquals(100, result);
        verify(userRepository).getTotalCount();
    }


    @Test
    void updateDisplayName_validInputs_shouldUpdateDisplayName() {
        // Arrange
        int id = 1;
        String newDisplayName = "newDisplayName";
        when(userRepository.updateDisplayName(id, newDisplayName)).thenReturn(1); // Assuming update returns the number of affected rows

        // Act
        int result = userService.updateDisplayName(id, newDisplayName);

        // Assert
        assertEquals(1, result);  // Verify that the method returns the expected result (1 affected row)
        verify(userRepository).updateDisplayName(id, newDisplayName);  // Ensure the repository method was called with the correct arguments
    }

    @Test
    void updateDisplayName_invalidId_shouldThrowIllegalArgumentException() {
        // Arrange
        int invalidId = 0;
        String newDisplayName = "newDisplayName";

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateDisplayName(invalidId, newDisplayName);
        });

        assertEquals("ID must be greater than zero.", thrown.getMessage());  // Ensure the correct message is thrown for invalid ID
    }

    @Test
    void updateDisplayName_negativeId_shouldThrowIllegalArgumentException() {
        // Arrange
        int invalidId = -1;
        String newDisplayName = "newDisplayName";

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateDisplayName(invalidId, newDisplayName);
        });

        assertEquals("ID must be greater than zero.", thrown.getMessage());  // Ensure the correct message is thrown for negative ID
    }

    @Test
    void updateDisplayName_nullDisplayName_shouldThrowIllegalArgumentException() {
        // Arrange
        int id = 1;
        String nullDisplayName = null;

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateDisplayName(id, nullDisplayName);
        });

        assertEquals("New display name cannot be null or empty.", thrown.getMessage());  // Verify the exception message for null display name
    }

    @Test
    void updateDisplayName_emptyDisplayName_shouldThrowIllegalArgumentException() {
        // Arrange
        int id = 1;
        String emptyDisplayName = "";

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateDisplayName(id, emptyDisplayName);
        });

        assertEquals("New display name cannot be null or empty.", thrown.getMessage());  // Verify the exception message for empty display name
    }



    @Test
    void getDisplayNameById_invalidId_shouldThrowIllegalArgumentException() {
        // Arrange
        int invalidId = 0;

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.getDisplayNameById(invalidId);
        });

        assertEquals("ID must be greater than zero.", thrown.getMessage());  // Verify the exception message for invalid ID
    }

    @Test
    void getDisplayNameById_negativeId_shouldThrowIllegalArgumentException() {
        // Arrange
        int invalidId = -1;

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.getDisplayNameById(invalidId);
        });

        assertEquals("ID must be greater than zero.", thrown.getMessage());  // Verify the exception message for negative ID
    }

    @Test
    void getDisplayNameById_validId_butNoDisplayName_shouldThrowUserRetrievalException() {
        // Arrange
        int id = 1;
        when(userRepository.getDisplayNameById(id)).thenReturn(null);  // Simulating that the repository returns null

        // Act & Assert
        UserRetrievalException thrown = assertThrows(UserRetrievalException.class, () -> {
            userService.getDisplayNameById(id);
        });

        assertEquals("Display name not found for user id: 1", thrown.getMessage());  // Verify the exception message for missing display name
    }


    @Test
    void getByUsername_nullUsername_shouldThrowIllegalArgumentException() {
        // Arrange
        String nullUsername = null;

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.getByUsername(nullUsername);
        });

        assertEquals("Username cannot be null or empty.", thrown.getMessage());  // Ensure the correct message is thrown for null username
    }

    @Test
    void getByUsername_emptyUsername_shouldThrowIllegalArgumentException() {
        // Arrange
        String emptyUsername = "";

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.getByUsername(emptyUsername);
        });

        assertEquals("Username cannot be null or empty.", thrown.getMessage());  // Ensure the correct message is thrown for empty username
    }

    @Test
    void getByUsername_validUsername_butNoUserFound_shouldReturnNull() {
        // Arrange
        String username = "nonExistentUsername";
        when(userRepository.getByUsername(username)).thenReturn(null);  // Simulate no user found in the repository

        // Act
        User result = userService.getByUsername(username);

        // Assert
        assertNull(result);  // Ensure that null is returned when no user is found
        verify(userRepository).getByUsername(username);  // Ensure the repository method was called with the correct username
    }


}

