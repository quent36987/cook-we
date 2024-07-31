package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.entity.UserDTO;
import com.cookwe.domain.service.RecipeService;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.UpdateUserRequest;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User operations")
public class UserController {

    private final UserService userService;
    private final RecipeService recipeService;

    public UserController(UserService userService, RecipeService recipeService) {
        this.userService = userService;
        this.recipeService = recipeService;
    }

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
    public List<RecipeDTO> getFavoriteRecipes() {
        return userService.getFavoriteRecipes(getUserId());
    }

    @Operation(summary = "Get my recipes")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/recipes")
    public List<RecipeDTO> getRecipes() {
        return recipeService.getRecipesByUserId(getUserId());
    }

    @Operation(summary = "Get user by username")
    @Parameter(name = "username", description = "The username of the user")
    @GetMapping("/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
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
    public List<RecipeDTO> getRecipesByUsername(@PathVariable String username) {
        return userService.getRecipesByUsername(username);
    }

    @Operation(summary = "Get my user details")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/details")
    public UserDTO getMyUserDetails() {
        return userService.getUserById(getUserId());
    }

    @Operation(summary = "Get user details (need ADMIN role)")
    @GetMapping("/{username}/details")
    @PreAuthorize("hasRole('ADMIN')") // FIXME test ?
    @Parameter(name = "username", description = "The username of the user")
    public UserDTO getUserDetails(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @Operation(summary = "Update user details (first name, last name)")
    @PutMapping("/details")
    @PreAuthorize("hasRole('USER')")
    @Parameter(name = "username", description = "The username of the user")
    public UserDTO updateUserDetails(@RequestBody UpdateUserRequest request) {
        return userService.updateUserDetails(getUserId(), request.firstName, request.lastName);
    }
}
