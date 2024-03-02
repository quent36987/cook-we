package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.RecipePictureModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipePictureRepository extends CrudRepository<RecipePictureModel, Long> {

    @Query("SELECT p FROM RecipePictureModel p WHERE p.recipe.id = :recipeId")
    public List<RecipePictureModel> findByRecipeId(Long recipeId);

    @Query("SELECT p FROM RecipePictureModel p WHERE p.recipe.id IN :recipeIds")
    public List<RecipePictureModel> findByRecipesId(List<Long> recipeIds);

    @Query("SELECT p FROM RecipePictureModel p WHERE p.imageUrl = :imageUrl")
    public Optional<RecipePictureModel> findByImageUrl(String imageUrl);
}
