package com.cookwe.domain.service;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.ShoppingListIngredientModel;
import com.cookwe.data.model.ShoppingListModel;
import com.cookwe.data.model.ShoppingListRecipeModel;
import com.cookwe.data.repository.interfaces.*;
import com.cookwe.domain.entity.RecipeShoppingListDTO;
import com.cookwe.domain.mapper.RecipeShoppingListMapper;
import com.cookwe.utils.errors.RestError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ShoppingListRecipeService {

    private final ShoppingListRepository shoppingListRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingListRecipeRepository shoppingListRecipeRepository;
    private final ShoppingListIngredientRepository shoppingListIngredientRepository;
    private final RecipeShoppingListMapper recipeShoppingListMapper;

    @Transactional
    public RecipeShoppingListDTO addOrUpdateRecipe(Long userId, Long recipeId, int portion, List<String> ingredients, Long shoppingListId) {
        ShoppingListModel shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> RestError.NOT_FOUND_MESSAGE.get("Shopping list not found"));

        if (!isUserAllowed(userId, shoppingList)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not allowed to modify this shopping list");
        }

        RecipeModel recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> RestError.NOT_FOUND_MESSAGE.get("Recipe not found"));

        ShoppingListRecipeModel existingRecipe = shoppingListRecipeRepository.findByShoppingListAndRecipe(shoppingList, recipe);

        if (existingRecipe != null) {
            shoppingListIngredientRepository.deleteByShoppingListRecipeId(existingRecipe.getId());

            existingRecipe.setPortion(portion);
            shoppingListRecipeRepository.save(existingRecipe);
        } else {
            ShoppingListRecipeModel newRecipe = new ShoppingListRecipeModel();
            newRecipe.setShoppingList(shoppingList);
            newRecipe.setRecipe(recipe);
            newRecipe.setPortion(portion);
            existingRecipe = shoppingListRecipeRepository.save(newRecipe);
        }

        addIngredientsToRecipe(existingRecipe, ingredients, shoppingList);

        return recipeShoppingListMapper.toDTO(existingRecipe);
    }

    private void addIngredientsToRecipe(ShoppingListRecipeModel shoppingListRecipe, List<String> ingredients, ShoppingListModel shoppingList) {
        for (String ingredientName : ingredients) {
            ShoppingListIngredientModel newIngredient = new ShoppingListIngredientModel();
            newIngredient.setShoppingListRecipe(shoppingListRecipe);
            newIngredient.setShoppingList(shoppingList);
            newIngredient.setName(ingredientName);
            newIngredient.setChecked(false);
            shoppingListIngredientRepository.save(newIngredient);
        }
    }

    public void removeRecipe(Long userId, Long recipeId, Long shoppingListId) {
        ShoppingListModel shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> RestError.NOT_FOUND_MESSAGE.get("Shopping list not found"));

        if (!isUserAllowed(userId, shoppingList)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not allowed to modify this shopping list");
        }

        ShoppingListRecipeModel existingRecipe = shoppingListRecipeRepository.findByShoppingListAndRecipe(shoppingList, recipeRepository.findById(recipeId)
                .orElseThrow(() -> RestError.NOT_FOUND_MESSAGE.get("Recipe not found")));

        if (existingRecipe != null) {
            shoppingListRecipeRepository.delete(existingRecipe);
        } else {
            throw RestError.NOT_FOUND_MESSAGE.get("Recipe not found in this shopping list");
        }
    }

    private boolean isUserAllowed(Long userId, ShoppingListModel shoppingList) {
        return shoppingList.getOwner().getId().equals(userId) ||
                shoppingList.getSharedWithUsers().stream().anyMatch(user -> user.getId().equals(userId));
    }
}
