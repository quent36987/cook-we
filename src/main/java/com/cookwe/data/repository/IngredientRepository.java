package com.cookwe.data.repository;

import com.cookwe.data.model.IngredientModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IngredientRepository extends CrudRepository<IngredientModel, Long> {

    @Query("SELECT i FROM IngredientModel i WHERE i.recipe.id = :recipeId")
    Iterable<IngredientModel> findByRecipeId(Long recipeId);

    @Query("SELECT i FROM IngredientModel i WHERE i.recipe.id = :recipeId AND i.name = :name")
    Optional<IngredientModel> findByRecipeIdAndName(Long recipeId, String name);

}
