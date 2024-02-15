package com.cookwe.data.repository;

import com.cookwe.data.model.CommentModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<CommentModel, Long> {

    @Query("SELECT c FROM CommentModel c WHERE c.recipe.id = :recipeId")
    Iterable<CommentModel> findByRecipeId(Long recipeId);

    @Query("SELECT c FROM CommentModel c WHERE c.user.id = :userId")
    Iterable<CommentModel> findByUserId(Long userId);

    @Query("SELECT c FROM CommentModel c WHERE c.user.username = :username")
    Iterable<CommentModel> findByUsername(String username);
}
