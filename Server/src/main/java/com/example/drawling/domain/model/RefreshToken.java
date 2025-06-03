package com.example.drawling.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RefreshToken {
    private int id;
    private String token;
    private Instant expiry;
    private User user;
}
