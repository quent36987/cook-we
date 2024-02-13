package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.UserEntity;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.presentation.response.UserResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
import com.cookwe.utils.converters.UserEntityToUserResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User operations")
public class UserController {

    @Autowired
    private UserService userService;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @Operation(summary = "Get user favorite recipes")
    @GetMapping("/favorites-recipes")
    public List<RecipeResponse> getFavoriteRecipes() {
        List<RecipeEntity> recipeEntities = userService.getFavoriteRecipes(getUserId());

        return RecipeEntityToRecipeResponse.convertList(recipeEntities);
    }

    @Operation(summary = "Get user recipes")
    @GetMapping("/recipes")
    public List<RecipeResponse> getRecipes() {
        List<RecipeEntity> recipeEntities = userService.getRecipes(getUserId());

        return RecipeEntityToRecipeResponse.convertList(recipeEntities);
    }

    @Operation(summary = "Get user by username")
    @Parameter(name = "username", description = "The username of the user")
    @GetMapping("/username/{username}")
    public UserResponse getUserByUsername(@PathVariable String username) {
        UserEntity userEntity = userService.getUserByUsername(username);

        return UserEntityToUserResponse.convert(userEntity);
    }

    @Operation(summary = "add recipe to user favorites recipes")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @PostMapping("/favorites-recipes/{recipeId}")
    public String addRecipeToFavorites(@PathVariable Long recipeId) {
        userService.addFavoriteRecipe(getUserId(), recipeId);

        return "Recipe added to favorites";
    }

    @Operation(summary = "delete recipe from user favorites recipes")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @DeleteMapping("/favorites-recipes/{recipeId}")
    public String deleteRecipeFromFavorites(@PathVariable Long recipeId) {
        userService.removeFavoriteRecipe(getUserId(), recipeId);

        return "Recipe deleted from favorites";
    }


}
