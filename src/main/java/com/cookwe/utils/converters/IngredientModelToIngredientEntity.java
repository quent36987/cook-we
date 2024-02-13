package com.cookwe.utils.converters;

import com.cookwe.data.model.IngredientModel;
import com.cookwe.domain.entity.IngredientEntity;

import java.util.ArrayList;
import java.util.List;

public class IngredientModelToIngredientEntity {
    public static IngredientEntity convert(IngredientModel ingredientModel) {
        return new IngredientEntity()
                .withId(ingredientModel.getId())
                .withName(ingredientModel.getName())
                .withQuantity(ingredientModel.getQuantity())
                .withUnit(ingredientModel.getUnit());
    }

    public static List<IngredientEntity> convertList(Iterable<IngredientModel> ingredientModels) {
        List<IngredientEntity> entities = new ArrayList<>();
        for (IngredientModel ingredientModel : ingredientModels) {
            entities.add(convert(ingredientModel));
        }
        return entities;
    }
}
