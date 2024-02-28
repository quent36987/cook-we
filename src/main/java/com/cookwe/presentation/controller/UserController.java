package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.UserEntity;
import com.cookwe.domain.service.RecipeService;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.presentation.response.UserDetailResponse;
import com.cookwe.presentation.response.UserResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
import com.cookwe.utils.converters.UserEntityToUserDetailResponse;
import com.cookwe.utils.converters.UserEntityToUserResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User operations")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @Operation(summary = "Get user favorite recipes")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/favorites-recipes")
    public List<RecipeResponse> getFavoriteRecipes() {
        List<RecipeEntity> recipeEntities = userService.getFavoriteRecipes(getUserId());



        return RecipeEntityToRecipeResponse.convertList(recipeEntities);
    }

    @Operation(summary = "Get my recipes")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/recipes")
    public List<RecipeResponse> getRecipes() {
        List<RecipeEntity> recipeEntities = recipeService.getRecipesByUserId(getUserId());

        return RecipeEntityToRecipeResponse.convertList(recipeEntities);
    }

    @Operation(summary = "Get user by username")
    @Parameter(name = "username", description = "The username of the user")
    @GetMapping("/{username}")
    public UserResponse getUserByUsername(@PathVariable String username) {
        UserEntity userEntity = userService.getUserByUsername(username);

        return UserEntityToUserResponse.convert(userEntity);
    }

    @Operation(summary = "add recipe to user favorites recipes")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/favorites-recipes/{recipeId}")
    public MessageResponse addRecipeToFavorites(@PathVariable Long recipeId) {
        userService.addFavoriteRecipe(getUserId(), recipeId);

        return new MessageResponse("Recipe added to favorites");
    }

    @Operation(summary = "delete recipe from user favorites recipes")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/favorites-recipes/{recipeId}")
    public MessageResponse deleteRecipeFromFavorites(@PathVariable Long recipeId) {
        userService.removeFavoriteRecipe(getUserId(), recipeId);

        return new MessageResponse("Recipe removed from favorites");
    }

    @Operation(summary = "Get user recipes with username")
    @Parameter(name = "username", description = "The username of the user")
    @GetMapping("/{username}/recipes")
    public List<RecipeResponse> getRecipesByUsername(@PathVariable String username) {
        List<RecipeEntity> recipeEntities = userService.getRecipesByUsername(username);

        return RecipeEntityToRecipeResponse.convertList(recipeEntities);
    }

    @Operation(summary = "Get my user details")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/details")
    public UserDetailResponse getMyUserDetails() {
        UserEntity userEntity = userService.getUserById(getUserId());

        return UserEntityToUserDetailResponse.convert(userEntity);
    }

    @Operation(summary = "Get user details (need ADMIN role)")
    @GetMapping("/{username}/details")
    @PreAuthorize("hasRole('ADMIN')") // FIXME test ?
    @Parameter(name = "username", description = "The username of the user")
    public UserDetailResponse getUserDetails(@PathVariable String username) {
        UserEntity userEntity = userService.getUserByUsername(username);

        return UserEntityToUserDetailResponse.convert(userEntity);
    }

    @Operation(summary = "Update user details (first name, last name)")
    @PutMapping("/details")
    @PreAuthorize("hasRole('USER')")
    @Parameter(name = "username", description = "The username of the user")
    public UserDetailResponse updateUserDetails(@RequestBody UserDetailResponse request) {
        UserEntity userEntity = userService.updateUserDetails(getUserId(), request.firstName, request.lastName);

        return UserEntityToUserDetailResponse.convert(userEntity);
    }
}
