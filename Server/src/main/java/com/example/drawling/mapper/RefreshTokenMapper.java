package com.example.drawling.mapper;

import com.example.drawling.domain.entity.RefreshTokenEntity;
import com.example.drawling.domain.model.RefreshToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    RefreshToken toModel(RefreshTokenEntity refreshTokenEntity);

    RefreshTokenEntity toEntity(RefreshToken refreshToken);

}
