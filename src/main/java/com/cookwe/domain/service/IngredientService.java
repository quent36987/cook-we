package com.cookwe.domain.service;

import com.cookwe.data.model.EUnit;
import com.cookwe.data.model.IngredientModel;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.repository.interfaces.IngredientRepository;
import com.cookwe.data.repository.RecipeRepositoryCustom;
import com.cookwe.domain.entity.IngredientDTO;
import com.cookwe.domain.mapper.IngredientMapper;
import com.cookwe.utils.errors.RestError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper;

    private final RecipeRepositoryCustom recipeRepositoryCustom;

    public IngredientService(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper, RecipeRepositoryCustom recipeRepositoryCustom) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.recipeRepositoryCustom = recipeRepositoryCustom;
    }

    @Transactional(readOnly = true)
    public List<IngredientDTO> getIngredientsByRecipeId(Long recipeId) {
        List<IngredientModel> ingredients = ingredientRepository.findByRecipeId(recipeId);

        return ingredientMapper.toDTOList(ingredients);
    }

    public IngredientDTO addIngredient(Long userId, Long recipeId, String name, Float quantity, String unit) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        return addIngredientToModel(userId, recipe, name, quantity, unit);
    }

    public IngredientDTO addIngredientToModel(Long userId, RecipeModel recipe, String name, Float quantity, String unit) {
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

            return ingredientMapper.toDTO(ingredientRepository.save(ingredient.get()));
        }

        IngredientModel ingredientModel = IngredientModel.builder()
                .name(name)
                .quantity(quantity)
                .recipe(recipe)
                .unit(EUnit.valueOf(unit)).build();

        return ingredientMapper.toDTO(ingredientRepository.save(ingredientModel));
    }

    public void deleteIngredient(Long userId, Long recipeId, String ingredientName) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

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
