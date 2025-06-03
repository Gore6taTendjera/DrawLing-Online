package com.example.drawling.application.controller;

import com.example.drawling.business.interfaces.service.UserService;
import com.example.drawling.domain.dto.UpdateDisplayNameRequestDTO;
import com.example.drawling.domain.dto.UserDTO;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.UserNotFoundException;
import com.example.drawling.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAll();
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(userDTOs);
    }


    @GetMapping(params = "name")
    public ResponseEntity<UserDTO> getUserByName(@RequestParam String name) {
        User user = userService.getByDisplayName(name);
        return user != null ? ResponseEntity.ok(userMapper.toDto(user)) : ResponseEntity.notFound().build();
    }

    @GetMapping(params = "username")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam String username) {
        User user = userService.getByUsername(username);
        return user != null ? ResponseEntity.ok(userMapper.toDto(user)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getById(id);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/new")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            User savedUser = userService.save(user);

            if (savedUser != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}/display-name")
    public ResponseEntity<String> getDisplayNameByUserId(@PathVariable int id) {
        return ResponseEntity.ok(userService.getDisplayNameById(id));
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @PatchMapping("/{userId}/display-name")
    public ResponseEntity<String> updateDisplayNameByUserId(@PathVariable("userId") int userId, @RequestBody UpdateDisplayNameRequestDTO request) {
        try {
            userService.updateDisplayName(userId, request.getDisplayName());
            return ResponseEntity.ok("Display name updated successfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update display name.");
        }
    }


    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalCount() {
        return ResponseEntity.ok(userService.getTotalCount());
    }

}
