package com.cookwe.utils.converters;

import com.cookwe.domain.entity.RecipeStepEntity;
import com.cookwe.presentation.response.RecipeStepResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepEntityTpRecipeStepResponse {
    public static RecipeStepResponse convert(RecipeStepEntity recipeStepEntity) {
        return new RecipeStepResponse()
                .withStepNumber(recipeStepEntity.getStepNumber())
                .withText(recipeStepEntity.getText());
    }

    public static List<RecipeStepResponse> convertList(List<RecipeStepEntity> recipeStepEntities) {
        List<RecipeStepResponse> responses = new ArrayList<>();
        for (RecipeStepEntity recipeStepEntity : recipeStepEntities) {
            responses.add(convert(recipeStepEntity));
        }
        return responses;
    }
}
