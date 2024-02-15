package com.cookwe.domain.service;

import com.cookwe.data.model.EUnit;
import com.cookwe.data.model.IngredientModel;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.repository.IngredientRepository;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.domain.entity.IngredientEntity;
import com.cookwe.utils.converters.IngredientModelToIngredientEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Data
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public RecipeModel getRecipeModelById(Long id) {
        Optional<RecipeModel> optionalRecipe = recipeRepository.findById(id);

        if (optionalRecipe.isEmpty()) {
            throw RestError.RECIPE_NOT_FOUND.get(id);
        }

        return optionalRecipe.get();
    }

    @Transactional
    public List<IngredientEntity> getIngredientsByRecipeId(Long recipeId) {
        Iterable<IngredientModel> ingredients = ingredientRepository.findByRecipeId(recipeId);

        return IngredientModelToIngredientEntity.convertList(ingredients);
    }

    @Transactional
    public IngredientEntity addIngredient(Long userId, Long recipeId, String name, Float quantity, String unit) {
        RecipeModel recipe = getRecipeModelById(recipeId);

        return addIngredientToModel(userId, recipe, name, quantity, unit);
    }

    public IngredientEntity addIngredientToModel(Long userId, RecipeModel recipe, String name, Float quantity, String unit) {
        try {
            EUnit.valueOf(unit);
        } catch (IllegalArgumentException e) {
            throw RestError.UNIT_NOT_FOUND.get(unit);
        }

        if (!Objects.equals(recipe.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of the recipe");
        }

        // find if name already exists in the recipe
        Optional<IngredientModel> ingredient = ingredientRepository.findByRecipeIdAndName(recipe.getId(), name);

        if (ingredient.isPresent()) {
            ingredient.get().setQuantity(quantity);
            ingredient.get().setUnit(EUnit.valueOf(unit));

            return IngredientModelToIngredientEntity.convert(ingredientRepository.save(ingredient.get()));
        }

        IngredientModel ingredientModel = new IngredientModel()
                .withName(name)
                .withQuantity(quantity)
                .withRecipe(recipe)
                .withUnit(EUnit.valueOf(unit));

        return IngredientModelToIngredientEntity.convert(ingredientRepository.save(ingredientModel));
    }

    @Transactional
    public void deleteIngredient(Long userId, Long recipeId, String ingredientName) {
        RecipeModel recipe = getRecipeModelById(recipeId);

        if (!Objects.equals(recipe.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of the recipe");
        }

        Optional<IngredientModel> ingredient = ingredientRepository.findByRecipeIdAndName(recipeId, ingredientName);

        if (ingredient.isEmpty()) {
            throw RestError.NOT_FOUND_MESSAGE.get(ingredientName);
        }

        ingredientRepository.delete(ingredient.get());
    }


}
