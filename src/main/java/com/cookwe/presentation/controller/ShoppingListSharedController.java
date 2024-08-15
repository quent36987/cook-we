package com.cookwe.presentation.controller;

import com.cookwe.domain.service.SharedShoppingListService;
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
@Tag(name = "ShoppingList", description = "Shared Shopping List operations")
public class ShoppingListSharedController {

    private final SharedShoppingListService sharedShoppingListService;

    public ShoppingListSharedController(SharedShoppingListService sharedShoppingListService) {
        this.sharedShoppingListService = sharedShoppingListService;
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @Operation(summary = "Add a user to a shopping list")
    @PostMapping("/{shoppingListId}/users/{usernameToAdd}")
    public MessageResponse addUserToShoppingList(@PathVariable Long shoppingListId, @PathVariable String usernameToAdd) {
        sharedShoppingListService.addUserToShoppingList(getUserId(), usernameToAdd, shoppingListId);
        return new MessageResponse("User added to shopping list");
    }

    @Operation(summary = "Remove a user from a shopping list")
    @DeleteMapping("/{shoppingListId}/users/{usernameToRemove}")
    public MessageResponse removeUserFromShoppingList(@PathVariable Long shoppingListId, @PathVariable String usernameToRemove) {
        sharedShoppingListService.removeUserFromShoppingList(getUserId(), usernameToRemove, shoppingListId);
        return new MessageResponse("User removed from shopping list");
    }
}
