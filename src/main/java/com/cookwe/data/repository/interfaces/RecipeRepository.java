package com.cookwe.data.repository.interfaces;


import com.cookwe.data.model.RecipeModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RecipeRepository extends CrudRepository<RecipeModel, Long> {
    @Query("SELECT r FROM RecipeModel r WHERE r.name = :name")
    public Optional<RecipeModel> findByName(@Param("name") String name);

    @Query("SELECT r FROM RecipeModel r WHERE r.user.id = :userId")
    List<RecipeModel> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM RecipeModel r JOIN IngredientModel i ON r.id = i.recipe.id WHERE i.name IN :names")
    Iterable<RecipeModel> findByRecipeByIngredientsName(List<String> names);
}