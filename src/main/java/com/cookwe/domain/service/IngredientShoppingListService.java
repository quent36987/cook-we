package com.cookwe.domain.service;

import com.cookwe.data.model.ShoppingListIngredientModel;
import com.cookwe.data.model.ShoppingListModel;
import com.cookwe.data.repository.interfaces.RecipeRepository;
import com.cookwe.data.repository.interfaces.ShoppingListIngredientRepository;
import com.cookwe.data.repository.interfaces.ShoppingListRepository;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.domain.entity.IngredientShoppingListDTO;
import com.cookwe.domain.mapper.IngredientShoppingListMapper;
import com.cookwe.utils.errors.RestError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class IngredientShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingListIngredientRepository shoppingListIngredientRepository;
    private final IngredientShoppingListMapper shoppingListIngredientMapper;

    @Transactional
    public IngredientShoppingListDTO addOrUpdateIngredient(Long userId, Long ingredientId, String name, Long shoppingListId) {
        ShoppingListModel shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> RestError.SHOPPING_LIST_NOT_FOUND.get(shoppingListId));

        if (isUserNotAllowed(userId, shoppingList)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not allowed to modify this shopping list");
        }

        if (ingredientId != null) {
            ShoppingListIngredientModel existingIngredient = shoppingListIngredientRepository.findById(ingredientId)
                    .orElseThrow(() -> RestError.NOT_FOUND_MESSAGE.get("Ingredient not found"));

            if (!existingIngredient.getShoppingList().getId().equals(shoppingListId)) {
                throw RestError.BAD_REQUEST_MESSAGE.get("Ingredient does not belong to this shopping list");
            }

            existingIngredient.setName(name);
            ShoppingListIngredientModel shoppingListIngredientModel = shoppingListIngredientRepository.save(existingIngredient);
            return shoppingListIngredientMapper.toDTO(shoppingListIngredientModel);
        } else {
            ShoppingListIngredientModel newIngredient = new ShoppingListIngredientModel();
            newIngredient.setShoppingList(shoppingList);
            newIngredient.setShoppingListRecipe(null);
            newIngredient.setName(name);
            newIngredient.setChecked(false);
            ShoppingListIngredientModel shoppingListIngredientModel = shoppingListIngredientRepository.save(newIngredient);
            return shoppingListIngredientMapper.toDTO(shoppingListIngredientModel);
        }
    }

    @Transactional
    public void checkOrUncheckIngredient(Long userId, Long ingredientId, boolean checked) {
        ShoppingListIngredientModel ingredient = shoppingListIngredientRepository.findById(ingredientId)
                .orElseThrow(() -> RestError.NOT_FOUND_MESSAGE.get("Ingredient not found"));

        if (isUserNotAllowed(userId, ingredient.getShoppingList())) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not allowed to modify this shopping list");
        }

        ingredient.setChecked(checked);
        shoppingListIngredientRepository.save(ingredient);
    }

    @Transactional
    public void deleteIngredient(Long userId, Long ingredientId) {
        ShoppingListIngredientModel ingredient = shoppingListIngredientRepository.findById(ingredientId)
                .orElseThrow(() -> RestError.NOT_FOUND_MESSAGE.get("Ingredient not found"));

        if (isUserNotAllowed(userId, ingredient.getShoppingList())) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not allowed to modify this shopping list");
        }

        shoppingListIngredientRepository.delete(ingredient);
    }


    private boolean isUserNotAllowed(Long userId, ShoppingListModel shoppingList) {
        return !shoppingList.getOwner().getId().equals(userId) &&
                shoppingList.getSharedWithUsers().stream().noneMatch(user -> user.getId().equals(userId));
    }


}
