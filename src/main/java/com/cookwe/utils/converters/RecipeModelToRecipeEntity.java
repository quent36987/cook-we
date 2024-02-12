package com.cookwe.utils.converters;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class RecipeModelToRecipeEntity {

//    public static RecipeEntity convert(RecipeModel recipeModel) {
//        return new RecipeEntity(
//                recipeModel.getId(),
//                recipeModel.getName(),
//                recipeModel.getTime(),
//                recipeModel.getSeason(),
//                recipeModel.getUser(),
//                recipeModel.getCreatedAt(),
//                recipeModel.getSteps(),
//                recipeModel.getFavoritedBy()
//        );
//    }

//    public static List<RecipeEntity> convertList(Iterable<RecipeModel> recipeModels) {
//        List<RecipeEntity> responses = new ArrayList<>();
//        for (RecipeModel recipeModel : recipeModels) {
//            responses.add(convert(recipeModel));
//        }
//        return responses;
//    }
}
