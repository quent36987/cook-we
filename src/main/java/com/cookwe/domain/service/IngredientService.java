package com.cookwe.domain.service;

import com.cookwe.data.model.IngredientModel;
import com.cookwe.data.repository.IngredientRepositoty;
import com.cookwe.domain.entity.IngredientEntity;
import com.cookwe.utils.converters.IngredientModelToIngredientEntity;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class IngredientService {

    @Autowired
    private IngredientRepositoty ingredientRepositoty;


    public List<IngredientEntity> getIngredientsByRecipeId(Long recipeId) {
        Iterable<IngredientModel> ingredients = ingredientRepositoty.findByRecipeId(recipeId);

        return IngredientModelToIngredientEntity.convertList(ingredients);
    }
}
