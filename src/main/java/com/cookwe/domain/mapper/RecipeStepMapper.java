package com.cookwe.domain.mapper;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.RecipeStepModel;
import com.cookwe.domain.entity.RecipeStepDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeStepMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "stepNumber", source = "stepNumber")
    @Mapping(target = "text", source = "text")
    RecipeStepDTO toDTO(RecipeStepModel step);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "stepNumber", source = "stepNumber")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "recipe", ignore = true)
    RecipeStepModel toModel(RecipeStepDTO step);

    List<RecipeStepDTO> toDTOList(List<RecipeStepModel> steps);

    List<RecipeStepModel> toModelList(List<RecipeStepDTO> steps);
}
