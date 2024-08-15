package com.cookwe.presentation.controller;

import com.cookwe.domain.service.IngredientShoppingListService;
import com.cookwe.presentation.request.ShoppingListIngredientRequest;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-list")
@Tag(name = "ShoppingList", description = "Ingredient Shopping List operations")
public class ShoppingListIngredientController {


    private final IngredientShoppingListService ingredientShoppingListService;

    public ShoppingListIngredientController(IngredientShoppingListService ingredientShoppingListService) {
        this.ingredientShoppingListService = ingredientShoppingListService;
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @Operation(summary = "Add or update an ingredient in a shopping list")
    @PostMapping("/{shoppingListId}/ingredients")
    public MessageResponse addOrUpdateIngredient(
            @PathVariable Long shoppingListId,
            @RequestBody ShoppingListIngredientRequest request) {
        ingredientShoppingListService.addOrUpdateIngredient(
                getUserId(),
                request.getId(),
                request.getName(),
                shoppingListId
        );
        return new MessageResponse("Ingredient added or updated successfully");
    }

    @Operation(summary = "Check or uncheck an ingredient")
    @PostMapping("/ingredients/{ingredientId}/check")
    public MessageResponse checkOrUncheckIngredient(
            @PathVariable Long ingredientId,
            @RequestParam boolean checked) {
        ingredientShoppingListService.checkOrUncheckIngredient(getUserId(), ingredientId, checked);
        return new MessageResponse("Ingredient checked status updated successfully");
    }

    @Operation(summary = "Delete an ingredient from a shopping list")
    @DeleteMapping("/ingredients/{ingredientId}")
    public MessageResponse deleteIngredient(@PathVariable Long ingredientId) {
        ingredientShoppingListService.deleteIngredient(getUserId(), ingredientId);
        return new MessageResponse("Ingredient deleted successfully");
    }
}
