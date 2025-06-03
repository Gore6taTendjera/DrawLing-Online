package com.example.drawling.repository.jpa;

import com.example.drawling.domain.entity.ImageEntity;
import com.example.drawling.domain.entity.SavedImageEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SavedImageRepositoryJPA extends CrudRepository<SavedImageEntity, Integer> {

    @Transactional(readOnly = true)
    @Query("SELECT d.image FROM SavedImageEntity d WHERE d.user.id = :userId")
    List<ImageEntity> findUserSavedImagesByUserId(@Param("userId") int id);

}
