package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.ShoppingListDTO;
import com.cookwe.domain.entity.ShoppingListDetailDTO;
import com.cookwe.domain.service.ShoppingListService;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/shopping-list")
@Tag(name = "ShoppingList", description = "Shopping List operations")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }


    @Operation(summary = "Get user's shopping lists")
    @GetMapping
    public List<ShoppingListDTO> getUserShoppingList() {
        return shoppingListService.getUserShoppingList(getUserId());
    }

    @Operation(summary = "Get a shopping list by ID")
    @GetMapping("/{shoppingListId}")
    public ShoppingListDetailDTO getShoppingListWithId(@PathVariable Long shoppingListId) {
        return shoppingListService.getShoppingListWithId(getUserId(), shoppingListId);
    }

    @Operation(summary = "Create a new empty shopping list")
    @PostMapping
    public ShoppingListDTO createEmptyShoppingList(@RequestBody String name) {
        return shoppingListService.createEmptyShoppingList(name, getUserId());
    }

    @Operation(summary = "Delete a shopping list")
    @DeleteMapping("/{shoppingListId}")
    public void deleteShoppingList(@PathVariable Long shoppingListId) {
        shoppingListService.deleteList(getUserId(), shoppingListId);
    }


}
