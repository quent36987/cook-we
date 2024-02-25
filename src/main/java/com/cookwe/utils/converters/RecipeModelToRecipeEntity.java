package com.cookwe.utils.converters;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeEntity;

import java.util.ArrayList;
import java.util.List;

public class RecipeModelToRecipeEntity {

    public static RecipeEntity convert(RecipeModel recipeModel) {
        return new RecipeEntity().withId(recipeModel.getId())
                .withName(recipeModel.getName())
                .withTime(recipeModel.getTime())
                .withPortions(recipeModel.getPortions())
                .withSeason(recipeModel.getSeason())
                .withType(recipeModel.getType())
                .withUser(UserModelToUserEntity.convert(recipeModel.getUser()))
                .withCreatedAt(recipeModel.getCreatedAt())
                .withSteps(RecipeStepModelToRecipeStepEntity.convertList(recipeModel.getSteps()));
    }

    public static List<RecipeEntity> convertList(Iterable<RecipeModel> recipeModels) {
        List<RecipeEntity> responses = new ArrayList<>();
        for (RecipeModel recipeModel : recipeModels) {
            responses.add(convert(recipeModel));
        }
        return responses;
    }
}
