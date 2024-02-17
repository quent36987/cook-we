package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.RecipeRepositoryCustom;
import com.cookwe.data.repository.UserRepositoryCustom;
import com.cookwe.data.repository.interfaces.RecipeRepository;
import com.cookwe.data.repository.interfaces.RoleRepository;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.UserEntity;
import com.cookwe.utils.converters.RecipeModelToRecipeEntity;
import com.cookwe.utils.converters.UserModelToUserEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Data
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    private RecipeRepositoryCustom recipeRepositoryCustom;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;


    @Transactional
    public UserEntity getUserById(Long id) {
        return UserModelToUserEntity.convert(userRepositoryCustom.getUserById(id));
    }

    @Transactional
    public List<RecipeEntity> getFavoriteRecipes(Long userId) {
        List<RecipeModel> favoriteRecipes = userRepository.findFavoriteRecipesByUserId(userId);

        return RecipeModelToRecipeEntity.convertList(favoriteRecipes);
    }

    @Transactional
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public UserEntity getUserByUsername(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return UserModelToUserEntity.convert(user.get());
    }

    @Transactional
    public Iterable<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public List<RecipeEntity> getRecipesByUsername(String username) {
        UserEntity user = getUserByUsername(username);

        List<RecipeModel> recipes = recipeRepository.findByUserId(user.getId());

        return RecipeModelToRecipeEntity.convertList(recipes);
    }

    @Transactional
    public void createUser(String username, String email, String password) {
        UserModel user = new UserModel(username, email, encoder.encode(password));
        List<RoleModel> roles = new ArrayList<>();

        RoleModel userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
    }


    @Transactional
    public UserEntity updateUserDetails(Long id, String firstName, String lastName) {
        UserModel user = userRepositoryCustom.getUserById(id);

        user.setFirstName(firstName);
        user.setLastName(lastName);

        userRepository.save(user);

        return UserModelToUserEntity.convert(user);
    }

    @Transactional
    public void addFavoriteRecipe(Long userId, Long recipeId) {
        UserModel user = userRepositoryCustom.getUserById(userId);
        RecipeModel favoriteRecipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        List<RecipeModel> favoriteRecipes = userRepository.findFavoriteRecipesByUserId(userId);

        if (favoriteRecipes.contains(favoriteRecipe)) {
            throw RestError.RECIPE_ALREADY_FAVORITE.get(favoriteRecipe.getName());
        }

        favoriteRecipes.add(favoriteRecipe);
        user.setFavoriteRecipes(favoriteRecipes);
    }

    @Transactional
    public void removeFavoriteRecipe(Long userId, Long recipeId) {
        UserModel user = userRepositoryCustom.getUserById(userId);
        RecipeModel favoriteRecipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        List<RecipeModel> favoriteRecipes = userRepository.findFavoriteRecipesByUserId(userId);

        if (!favoriteRecipes.contains(favoriteRecipe)) {
            throw RestError.RECIPE_NOT_FAVORITE.get(favoriteRecipe.getName());
        }

        favoriteRecipes.remove(favoriteRecipe);
        user.setFavoriteRecipes(favoriteRecipes);
    }
}
