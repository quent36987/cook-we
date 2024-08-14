package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.RoleModel;
import com.cookwe.data.model.UserModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u.favoriteRecipes FROM UserModel u WHERE u.id = :userId")
    List<RecipeModel> findFavoriteRecipesByUserId(Long userId);

    @Query("SELECT u.roles FROM UserModel u WHERE u.id = :userId")
    List<RoleModel> findRolesByUserId(Long userId);

    @Query("SELECT 1 FROM UserModel u JOIN u.favoriteRecipes r WHERE u.id = :userId AND r.id = :recipeId")
    Boolean isUserLikedRecipe(Long userId, Long recipeId);

}
