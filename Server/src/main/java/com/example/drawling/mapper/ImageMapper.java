package com.example.drawling.mapper;

import com.example.drawling.domain.dto.ImageDTO;
import com.example.drawling.domain.entity.ImageEntity;
import com.example.drawling.domain.model.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDTO toDto(Image image);

    Image toModel(ImageEntity imageEntity);

    ImageEntity toEntity(Image image);

}
