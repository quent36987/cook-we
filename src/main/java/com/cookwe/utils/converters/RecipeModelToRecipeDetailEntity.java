package com.cookwe.utils.converters;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeDetailEntity;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.presentation.response.RecipeResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipeModelToRecipeDetailEntity {
    public static RecipeDetailEntity convert(RecipeModel recipeModel) {
        return new RecipeDetailEntity()
                .withId(recipeModel.getId())
                .withName(recipeModel.getName())
                .withTime(recipeModel.getTime())
                .withPortions(recipeModel.getPortions())
                .withSeason(recipeModel.getSeason())
                .withType(recipeModel.getType())
                .withUser(UserModelToUserEntity.convert(recipeModel.getUser()))
                .withCreatedAt(recipeModel.getCreatedAt())
                .withSteps(RecipeStepModelToRecipeStepEntity.convertList(recipeModel.getSteps()));
    }

    public static List<RecipeDetailEntity> convertList(Iterable<RecipeModel> recipeModels) {
        List<RecipeDetailEntity> entities = new ArrayList<>();
        for (RecipeModel recipeModel : recipeModels) {
            entities.add(convert(recipeModel));
        }
        return entities;
    }
}
