package com.cookwe.presentation.controller;

import com.cookwe.domain.service.ShoppingListRecipeService;
import com.cookwe.presentation.request.ShoppingListRecipeRequest;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-list")
@Tag(name = "ShoppingList", description = "Recipe Shopping List operations")
public class ShoppingListRecipeController {

    private final ShoppingListRecipeService shoppingListRecipeService;

    public ShoppingListRecipeController(ShoppingListRecipeService shoppingListRecipeService) {
        this.shoppingListRecipeService = shoppingListRecipeService;
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @Operation(summary = "Add or update a recipe in a shopping list")
    @PostMapping("/{shoppingListId}/recipes")
    public MessageResponse addOrUpdateRecipe(
            @PathVariable Long shoppingListId,
            @RequestBody ShoppingListRecipeRequest request) {
        shoppingListRecipeService.addOrUpdateRecipe(
                getUserId(),
                request.getRecipeId(),
                request.getPortion(),
                request.getIngredients(),
                shoppingListId
        );
        return new MessageResponse("Recipe added or updated successfully");
    }

    @Operation(summary = "Remove a recipe from a shopping list")
    @DeleteMapping("/{shoppingListId}/recipes/{recipeId}")
    public MessageResponse removeRecipe(
            @PathVariable Long shoppingListId,
            @PathVariable Long recipeId) {
        shoppingListRecipeService.removeRecipe(getUserId(), recipeId, shoppingListId);
        return new MessageResponse("Recipe removed successfully");
    }
}
