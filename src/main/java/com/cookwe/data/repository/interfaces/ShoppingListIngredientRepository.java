package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.ShoppingListIngredientModel;
import com.cookwe.data.model.ShoppingListRecipeModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListIngredientRepository extends CrudRepository<ShoppingListIngredientModel, Long> {

    Optional<ShoppingListIngredientModel> findById(Long id);

    List<ShoppingListIngredientModel> findByShoppingListRecipe(ShoppingListRecipeModel shoppingListRecipe);

    @Modifying
    @Query("DELETE FROM ShoppingListIngredientModel s WHERE s.shoppingListRecipe.id = :shoppingListRecipeId")
    void deleteByShoppingListRecipeId(Long shoppingListRecipeId);
}
