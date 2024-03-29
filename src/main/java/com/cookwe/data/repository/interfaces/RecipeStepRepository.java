package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.RecipeStepModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStepModel, Long> {

    @Query("SELECT rs FROM RecipeStepModel rs WHERE rs.recipe.id = :recipeId")
    List<RecipeStepModel> findByRecipeId(Long recipeId);
}
