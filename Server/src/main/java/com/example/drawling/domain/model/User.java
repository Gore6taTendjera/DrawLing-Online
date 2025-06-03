package com.example.drawling.domain.model;

import com.example.drawling.domain.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends Player {
    private String email;
    private String username;
    private String password;
    private Image profilePicture;
    private Role role;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, String displayName, String email, String password, String username) {
        super(id, displayName); // Pass id and displayName to the superclass constructor
        this.email = email;
        this.password = password;
        this.username = username;
    }
}