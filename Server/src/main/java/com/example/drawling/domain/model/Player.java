package com.example.drawling.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

// not registered player
// Base class for all players
@Getter
@Setter
@MappedSuperclass
public class Player {
    @Column(name = "id")
    private int id;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "experience")
    private int experience;

    @Column(name = "balance")
    private double balance;

    @Transient // ignore this field for db
    private String profilePictureUrl;

    public Player() {}

    public Player(String displayName) {
        this.displayName = displayName;
    }

    public Player(int userId, String displayName) {
        this.id = userId;
        this.displayName = displayName;
    }

    public Player(String displayName, double balance, String profilePictureUrl) {
        this.displayName = displayName;
        this.balance = balance;
        this.profilePictureUrl = profilePictureUrl;
    }
}
