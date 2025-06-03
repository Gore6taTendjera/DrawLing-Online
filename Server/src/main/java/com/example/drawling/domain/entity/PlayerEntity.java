package com.example.drawling.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class PlayerEntity {
    @Column(name = "display_name")
    private String displayName;

    @Column(name = "experience")
    private int experience;

    @Column(name = "balance")
    private double balance;

    public PlayerEntity() {}

    public PlayerEntity(String displayName) {
        this.displayName = displayName;
    }
}
