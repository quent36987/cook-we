package com.cookwe.utils.converters;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.presentation.response.RecipeResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipeEntityToRecipeResponse {
    public static RecipeResponse convert(RecipeEntity recipeEntity) {
        return new RecipeResponse()
                .withId(recipeEntity.getId())
                .withName(recipeEntity.getName())
                .withTime(recipeEntity.getTime())
                .withPortions(recipeEntity.getPortions())
                .withSeason(recipeEntity.getSeason())
                .withType(recipeEntity.getType())
                .withUser(UserEntityToUserResponse.convert(recipeEntity.getUser()))
                .withCreatedAt(recipeEntity.getCreatedAt())
                .withPictures(RecipePictureEntityToRecipePictureReponse.convertList(recipeEntity.getPictures()))
                .withSteps(RecipeStepEntityToRecipeStepResponse.convertList(recipeEntity.getSteps()));

    }

    public static List<RecipeResponse> convertList(Iterable<RecipeEntity> recipeEntities) {
        List<RecipeResponse> responses = new ArrayList<>();
        for (RecipeEntity recipeEntity : recipeEntities) {
            responses.add(convert(recipeEntity));
        }
        return responses;
    }
}
