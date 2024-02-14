package com.cookwe.data.repository;

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

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u.favoriteRecipes FROM UserModel u WHERE u.id = :userId")
    List<RecipeModel> findFavoriteRecipesByUserId(Long userId);

    @Query("SELECT u.roles FROM UserModel u WHERE u.id = :userId")
    List<RoleModel> findRolesByUserId(Long userId);
}
