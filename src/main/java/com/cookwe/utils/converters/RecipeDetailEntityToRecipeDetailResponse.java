package com.cookwe.utils.converters;

import com.cookwe.domain.entity.RecipeDetailEntity;
import com.cookwe.presentation.response.RecipeDetailResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailEntityToRecipeDetailResponse {
    public static RecipeDetailResponse convert(RecipeDetailEntity recipeDetailEntity) {
        return RecipeDetailResponse.builder()
                .id(recipeDetailEntity.getId())
                .name(recipeDetailEntity.getName())
                .time(recipeDetailEntity.getTime())
                .portions(recipeDetailEntity.getPortions())
                .season(recipeDetailEntity.getSeason())
                .user(UserEntityToUserResponse.convert(recipeDetailEntity.getUser()))
                .createdAt(recipeDetailEntity.getCreatedAt())
                .type(recipeDetailEntity.getType())
                .ingredients(IngredientEntityToIngredientResponse.convertList(recipeDetailEntity.getIngredients()))
                .pictures(RecipePictureEntityToRecipePictureReponse.convertList(recipeDetailEntity.getPictures()))
                .comments(CommentEntityToCommentResponse.convertList(recipeDetailEntity.getComments()))
                .steps(RecipeStepEntityToRecipeStepResponse.convertList(recipeDetailEntity.getSteps()))
                .build();
    }

    public static List<RecipeDetailResponse> convertList(Iterable<RecipeDetailEntity> recipeDetailEntities) {
        List<RecipeDetailResponse> responses = new ArrayList<>();
        for (RecipeDetailEntity recipeDetailEntity : recipeDetailEntities) {
            responses.add(convert(recipeDetailEntity));
        }
        return responses;
    }
}
