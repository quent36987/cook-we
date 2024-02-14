package com.cookwe.utils.converters;

import com.cookwe.data.model.RecipeStepModel;
import com.cookwe.domain.entity.RecipeStepEntity;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepModelToRecipeStepEntity {
    public static RecipeStepEntity convert(RecipeStepModel recipeStepModel) {
        return new RecipeStepEntity().withId(recipeStepModel.getId())
                .withStepNumber(recipeStepModel.getStepNumber())
                .withText(recipeStepModel.getText())
                .withRecipeId(recipeStepModel.getRecipe().getId());
    }

    public static List<RecipeStepEntity> convertList(Iterable<RecipeStepModel> recipeStepModels) {
        List<RecipeStepEntity> responses = new ArrayList<>();
        for (RecipeStepModel recipeStepModel : recipeStepModels) {
            responses.add(convert(recipeStepModel));
        }
        return responses;
    }
}
