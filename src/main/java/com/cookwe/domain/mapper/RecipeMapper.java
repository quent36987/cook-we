package com.cookwe.domain.mapper;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = {RecipePictureMapper.class})
public interface RecipeMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "time", source = "time")
    @Mapping(target = "portions", source = "portions")
    @Mapping(target = "season", source = "season")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "pictures", source = "pictures")
    RecipeDTO toDTO(RecipeModel recipe);

    List<RecipeDTO> toDTOList(List<RecipeModel> recipes);
}
