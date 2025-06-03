package com.example.drawling.mapper;

import com.example.drawling.domain.entity.SavedImageEntity;
import com.example.drawling.domain.model.SavedImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SavedImageMapper {

    SavedImage toModel(SavedImageEntity savedImageEntity);

    SavedImageEntity toEntity(SavedImage savedImage);
}
