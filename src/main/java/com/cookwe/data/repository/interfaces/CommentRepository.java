package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.CommentModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<CommentModel, Long> {

    @Query("SELECT c FROM CommentModel c WHERE c.recipe.id = :recipeId")
    List<CommentModel> findByRecipeId(Long recipeId);

    @Query("SELECT c FROM CommentModel c WHERE c.user.id = :userId")
    List<CommentModel> findByUserId(Long userId);

    @Query("SELECT c FROM CommentModel c WHERE c.user.username = :username")
    List<CommentModel> findByUsername(String username);
}
