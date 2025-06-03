package com.example.drawling.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class Image {
    private int id;
    private String name;
    private String path;
    private String type;
    private ZonedDateTime createdAt;

    public Image() {}

    public Image(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Image(String name, String path, String type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public Image(int id, String path, String type) {
        this.id = id;
        this.name = path;
        this.path = type;
    }

    public Image(String name, String path, String type, ZonedDateTime createdAt) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Image(int id, String imageName, String imagePath, String imageType) {
        this.id = id;
        this.name = imageName;
        this.path = imagePath;
        this.type = imageType;
    }

    public Image(int id, String imageName, String imagePath, String imageType, ZonedDateTime createdAt) {
        this.id = id;
        this.name = imageName;
        this.path = imagePath;
        this.type = imageType;
        this.createdAt = createdAt;
    }

}
