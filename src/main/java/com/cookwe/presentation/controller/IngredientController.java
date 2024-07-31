package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.IngredientDTO;
import com.cookwe.domain.service.IngredientService;
import com.cookwe.presentation.request.IngredientRequest;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredient", description = "Ingredient operations")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @GetMapping("/recipes/{recipeId}")
    @Operation(summary = "Get all ingredients")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    public List<IngredientDTO> getIngredientsByRecipeId(@PathVariable Long recipeId) {
        return ingredientService.getIngredientsByRecipeId(recipeId);
    }

    @PutMapping("/recipes/{recipeId}")
    @Operation(summary = "Add or update a new ingredient to a recipe")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @PreAuthorize("hasRole('USER')")
    public IngredientDTO addIngredient(@PathVariable Long recipeId, @RequestBody IngredientRequest request) {
        if (request.name == null || request.name.isEmpty()) {
            throw RestError.MISSING_FIELD.get("name");
        }

        if (request.quantity == null || request.quantity.isNaN() || request.quantity <= 0) {
            throw RestError.MISSING_FIELD.get("quantity");
        }

        if (request.unit == null || request.unit.isEmpty()) {
            throw RestError.MISSING_FIELD.get("unit");
        }

        return ingredientService.addIngredient(getUserId(), recipeId, request.name, request.quantity, request.unit);
    }

    @DeleteMapping("/recipes/{recipeId}/{ingredientName}")
    @Operation(summary = "Delete an ingredient from a recipe")
    @Parameter(name = "ingredientName", description = "The name of the ingredient")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteIngredient(@PathVariable String ingredientName, @PathVariable Long recipeId) {
        ingredientService.deleteIngredient(getUserId(), recipeId, ingredientName);

        return ResponseEntity.noContent().build();
    }


}
