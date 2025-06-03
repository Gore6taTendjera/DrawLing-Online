package com.example.drawling.mapper;

import com.example.drawling.domain.dto.UserDTO;
import com.example.drawling.domain.entity.UserEntity;
import com.example.drawling.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Mapping User to UserDTO (only displayName is present in UserDTO)
    @Mapping(source = "displayName", target = "displayName")
    UserDTO toDto(User user);

    // Mapping UserDTO to User (only displayName is present in UserDTO)
    @Mapping(source = "displayName", target = "displayName")
    User toModel(UserDTO userDTO);

    // Mapping UserEntity to User (all properties needed)
    @Mapping(source = "displayName", target = "displayName")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "profilePicture", target = "profilePicture")
    User toModel(UserEntity userEntity);

    // Mapping User to UserEntity (all properties needed)
    @Mapping(source = "displayName", target = "displayName")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "profilePicture", target = "profilePicture")
    UserEntity toEntity(User user);
}
