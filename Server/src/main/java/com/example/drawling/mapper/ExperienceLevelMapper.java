package com.example.drawling.mapper;

import com.example.drawling.domain.dto.ExperienceLevelDTO;
import com.example.drawling.domain.model.profile.ExperienceLevel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceLevelMapper {

    ExperienceLevelDTO toDto(ExperienceLevel experienceLevel);

    ExperienceLevel toModel(ExperienceLevelDTO experienceLevelDTO);
}
