package com.cookwe.utils.converters;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.presentation.response.RecipeResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipeEntityToRecipeResponse {
    public static RecipeResponse convert(RecipeEntity recipeEntity) {
        return new RecipeResponse(
                recipeEntity.id,
                recipeEntity.name,
                recipeEntity.time,
                recipeEntity.season,
                recipeEntity.user,
                recipeEntity.createdAt,
                recipeEntity.steps,
                recipeEntity.favoritedBy
        );
    }

    public static List<RecipeResponse> convertList(List<RecipeEntity> recipeEntities) {
        List<RecipeResponse> responses = new ArrayList<>();
        for (RecipeEntity recipeEntity : recipeEntities) {
            responses.add(convert(recipeEntity));
        }
        return responses;
    }
}
