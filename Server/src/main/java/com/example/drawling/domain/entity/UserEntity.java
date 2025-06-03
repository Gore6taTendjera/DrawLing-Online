package com.example.drawling.domain.entity;

import com.example.drawling.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity extends PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "user_role")
    private Role role;


    @ManyToOne
    @JoinColumn(name = "profile_picture", referencedColumnName = "id")
    private ImageEntity profilePicture;

    public UserEntity() {}

    public UserEntity(int id, String displayName, String email, String password, String username) {
        super(displayName);
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
    }

}
