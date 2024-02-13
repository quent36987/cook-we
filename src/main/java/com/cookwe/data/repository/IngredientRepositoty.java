package com.cookwe.data.repository;

import com.cookwe.data.model.IngredientModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepositoty extends CrudRepository<IngredientRepositoty, Long> {

    @Query("SELECT i FROM IngredientModel i WHERE i.recipe.id = :recipeId")
    Iterable<IngredientModel> findByRecipeId(Long recipeId);
}
