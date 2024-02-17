package com.cookwe.data.repository;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.repository.interfaces.RecipeRepository;
import com.cookwe.utils.errors.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RecipeRepositoryCustom {

    @Autowired
    private RecipeRepository recipeRepository;

    public RecipeModel getRecipeModelById(Long id) {
        Optional<RecipeModel> optionalRecipe = recipeRepository.findById(id);

        if (optionalRecipe.isEmpty()) {
            throw RestError.RECIPE_NOT_FOUND.get(id);
        }

        return optionalRecipe.get();
    }
}