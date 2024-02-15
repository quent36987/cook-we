package com.cookwe.data.repository;


import com.cookwe.data.model.IngredientModel;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends CrudRepository<RecipeModel, Long> {
    //    Optional<RecipeModel> findByName(String name);
    @Query("SELECT r FROM RecipeModel r WHERE r.name = :name")
    Optional<RecipeModel> findByName(@Param("name") String name);

    @Query("SELECT r FROM RecipeModel r WHERE r.user.id = :userId")
    List<RecipeModel> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM RecipeModel r JOIN IngredientModel i ON r.id = i.recipe.id WHERE i.name IN :names")
    Iterable<RecipeModel> findByRecipeByIngredientsName(List<String> names);
}
