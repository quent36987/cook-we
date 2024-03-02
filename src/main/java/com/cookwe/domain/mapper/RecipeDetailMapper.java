package com.cookwe.domain.mapper;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {IngredientMapper.class, CommentMapper.class, RecipeStepMapper.class, RecipePictureMapper.class})
public interface RecipeDetailMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "time", source = "time")
    @Mapping(target = "portions", source = "portions")
    @Mapping(target = "season", source = "season")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "ownerUsername", source = "user.username")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "steps", source = "steps")
    @Mapping(target = "pictures", source = "pictures")
    @Mapping(target = "ingredients", source = "ingredients")
    @Mapping(target = "comments", source = "comments")
    RecipeDetailDTO toDTO(RecipeModel recipe);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "time", source = "time")
    @Mapping(target = "portions", source = "portions")
    @Mapping(target = "season", source = "season")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "user.username", source = "ownerUsername")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "steps", source = "steps")
    @Mapping(target = "pictures", source = "pictures")
    @Mapping(target = "ingredients", source = "ingredients")
    @Mapping(target = "comments", source = "comments")
    RecipeModel toModel(RecipeDetailDTO recipe);

    List<RecipeDetailDTO> toDTOList(List<RecipeModel> recipes);

    List<RecipeModel> toModelList(List<RecipeDetailDTO> recipes);
}
