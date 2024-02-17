package com.cookwe.utils.converters;

import com.cookwe.domain.entity.RecipeDetailEntity;
import com.cookwe.presentation.response.RecipeDetailResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailEntityToRecipeDetailResponse {
    public static RecipeDetailResponse convert(RecipeDetailEntity recipeDetailEntity) {
        return new RecipeDetailResponse()
                .withId(recipeDetailEntity.getId())
                .withName(recipeDetailEntity.getName())
                .withTime(recipeDetailEntity.getTime())
                .withPortions(recipeDetailEntity.getPortions())
                .withSeason(recipeDetailEntity.getSeason())
                .withUser(UserEntityToUserResponse.convert(recipeDetailEntity.getUser()))
                .withCreatedAt(recipeDetailEntity.getCreatedAt())
                .withIngredients(IngredientEntityToIngredientResponse.convertList(recipeDetailEntity.getIngredients()))
                .withPictures(RecipePictureEntityToRecipePictureReponse.convertList(recipeDetailEntity.getPictures()))
                .withComments(CommentEntityToCommentResponse.convertList(recipeDetailEntity.getComments()))
                .withSteps(RecipeStepEntityToRecipeStepResponse.convertList(recipeDetailEntity.getSteps()));
    }

    public static List<RecipeDetailResponse> convertList(Iterable<RecipeDetailEntity> recipeDetailEntities) {
        List<RecipeDetailResponse> responses = new ArrayList<>();
        for (RecipeDetailEntity recipeDetailEntity : recipeDetailEntities) {
            responses.add(convert(recipeDetailEntity));
        }
        return responses;
    }
}
