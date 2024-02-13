package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.data.repository.RoleRepository;
import com.cookwe.data.repository.UserRepository;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.UserEntity;
import com.cookwe.utils.converters.RecipeModelToRecipeEntity;
import com.cookwe.utils.converters.UserEntityToUserResponse;
import com.cookwe.utils.converters.UserModelToUserEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Data
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    public void createUser(String username, String email, String password) {
        UserModel user = new UserModel(username, email, encoder.encode(password));
        Set<RoleModel> roles = new HashSet<>();

        RoleModel userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
    }

    public UserModel getUserById(Long id) {
        Optional<UserModel> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return user.get();
    }

    public RecipeModel getRecipeById(Long id) {
        Optional<RecipeModel> recipe = recipeRepository.findById(id);

        if (recipe.isEmpty()) {
            throw RestError.RECIPE_NOT_FOUND.get(id);
        }

        return recipe.get();
    }

    public void addFavoriteRecipe(Long userId, Long recipeId) {
        UserModel user = getUserById(userId);
        RecipeModel favoriteRecipe = getRecipeById(recipeId);

        if (user.getFavoriteRecipes().contains(favoriteRecipe)) {
            throw RestError.RECIPE_ALREADY_FAVORITE.get(favoriteRecipe.getName());
        }

        user.addFavoriteRecipe(favoriteRecipe);
    }

    public void removeFavoriteRecipe(Long userId, Long recipeId) {
        UserModel user = getUserById(userId);
        RecipeModel favoriteRecipe = getRecipeById(recipeId);

        if (!user.getFavoriteRecipes().remove(favoriteRecipe)) {
            throw RestError.RECIPE_NOT_FAVORITE.get(favoriteRecipe.getName());
        }
    }

    public List<RecipeEntity> getFavoriteRecipes(Long userId) {
        List<RecipeModel> favoriteRecipes = userRepository.findFavoriteRecipesByUserId(userId);

        return RecipeModelToRecipeEntity.convertList(favoriteRecipes);
    }

    public List<RecipeEntity> getRecipes(Long userId) {
        List<RecipeModel> recipes = recipeRepository.findByUserId(userId);

        return RecipeModelToRecipeEntity.convertList(recipes);
    }

    public UserEntity getUserByUsername(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return UserModelToUserEntity.convert(user.get());
    }


    public Iterable<UserModel> getAllUsers() {
        return userRepository.findAll();
    }
}
