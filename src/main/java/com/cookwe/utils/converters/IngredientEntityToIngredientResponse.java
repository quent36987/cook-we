package com.cookwe.utils.converters;

import com.cookwe.domain.entity.IngredientEntity;
import com.cookwe.presentation.response.IngredientResponse;

import java.util.ArrayList;
import java.util.List;

public class IngredientEntityToIngredientResponse {
    public static IngredientResponse convert(IngredientEntity ingredientEntity) {
        return new IngredientResponse()
                .withName(ingredientEntity.getName())
                .withQuantity(ingredientEntity.getQuantity())
                .withUnit(ingredientEntity.getUnit());
    }

    public static List<IngredientResponse> convertList(List<IngredientEntity> ingredientEntities) {
        List<IngredientResponse> responses = new ArrayList<>();
        for (IngredientEntity ingredientEntity : ingredientEntities) {
            responses.add(convert(ingredientEntity));
        }
        return responses;
    }
}
