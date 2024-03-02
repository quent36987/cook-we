package com.cookwe.domain.mapper;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.RecipeStepModel;
import com.cookwe.domain.entity.RecipeStepDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeStepMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "recipeId", source = "recipe.id")
    @Mapping(target = "stepNumber", source = "stepNumber")
    @Mapping(target = "text", source = "text")
    RecipeStepDTO toDTO(RecipeStepModel step);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "stepNumber", source = "stepNumber")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "recipe.id", source = "recipeId")
    RecipeStepModel toModel(RecipeStepDTO step);

//    @Mapping(target = "id", source = "id")
//    @Mapping(target = "recipeId", source = "recipeId")
//    @Mapping(target = "stepNumber", source = "stepNumber")
//    @Mapping(target = "text", source = "text")
//    void updateFromDTO(RecipeStepDTO step, @MappingTarget RecipeStepModel model);
//
//    default RecipeStepModel toModel(RecipeStepDTO step, Long recipeId) {
//        RecipeStepModel model = new RecipeStepModel();
//        updateFromDTO(step, model);
//        model.setRecipe(new RecipeModel().withId(recipeId));
//        return model;
//    }

    List<RecipeStepDTO> toDTOList(List<RecipeStepModel> steps);

    List<RecipeStepModel> toModelList(List<RecipeStepDTO> steps);
}
