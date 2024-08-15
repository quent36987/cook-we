package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.ShoppingListModel;
import com.cookwe.data.model.ShoppingListRecipeModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingListRecipeRepository extends CrudRepository<ShoppingListRecipeModel, Long> {

    @EntityGraph(attributePaths = {"recipe.pictures"})
    @Query("SELECT s FROM ShoppingListRecipeModel s WHERE s.id = :id")
    Optional<ShoppingListRecipeModel> findDetailById(@Param("id") Long id);

    ShoppingListRecipeModel findByShoppingListAndRecipe(ShoppingListModel shoppingList, RecipeModel recipe);
}

