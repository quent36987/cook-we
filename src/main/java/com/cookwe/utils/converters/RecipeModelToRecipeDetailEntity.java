package com.cookwe.utils.converters;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeDetailEntity;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.presentation.response.RecipeResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipeModelToRecipeDetailEntity {
    public static RecipeDetailEntity convert(RecipeModel recipeModel) {
        return RecipeDetailEntity.builder()
                .id(recipeModel.getId())
                .name(recipeModel.getName())
                .time(recipeModel.getTime())
                .portions(recipeModel.getPortions())
                .season(recipeModel.getSeason())
                .type(recipeModel.getType())
                .user(UserModelToUserEntity.convert(recipeModel.getUser()))
                .createdAt(recipeModel.getCreatedAt())
                .steps(RecipeStepModelToRecipeStepEntity.convertList(recipeModel.getSteps()))
                .pictures(RecipePictureModelToRecipePictureEntity.convertList(recipeModel.getPictures()))
                .build();
    }

    public static List<RecipeDetailEntity> convertList(Iterable<RecipeModel> recipeModels) {
        List<RecipeDetailEntity> entities = new ArrayList<>();
        for (RecipeModel recipeModel : recipeModels) {
            entities.add(convert(recipeModel));
        }
        return entities;
    }
}
