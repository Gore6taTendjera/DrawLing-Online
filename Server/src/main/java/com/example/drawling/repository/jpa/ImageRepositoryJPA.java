package com.example.drawling.repository.jpa;

import com.example.drawling.domain.entity.ImageEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ImageRepositoryJPA extends CrudRepository<ImageEntity, Integer> {

    @Transactional(readOnly = true)
    Optional<ImageEntity> findByName(String name);

    @Transactional(readOnly = true)
    @Query("SELECT pp FROM UserEntity u JOIN u.profilePicture pp WHERE u.id = :id")
    Optional<ImageEntity> findUserProfilePictureById(Integer id);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.profilePicture.id = :imageId WHERE u.id = :userId")
    int setImageProfilePicture(@Param("userId") int userId, @Param("imageId") int imageId);

}
