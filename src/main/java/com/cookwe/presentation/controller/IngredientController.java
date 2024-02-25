package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.IngredientEntity;
import com.cookwe.domain.service.IngredientService;
import com.cookwe.presentation.request.IngredientRequest;
import com.cookwe.presentation.response.IngredientResponse;
import com.cookwe.utils.converters.IngredientEntityToIngredientResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredient", description = "Ingredient operations")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @GetMapping("/recipes/{recipeId}")
    @Operation(summary = "Get all ingredients")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    public List<IngredientResponse> getIngredientsByRecipeId(@PathVariable Long recipeId) {
        List<IngredientEntity> ingredients = ingredientService.getIngredientsByRecipeId(recipeId);

        return IngredientEntityToIngredientResponse.convertList(ingredients);
    }

    @PutMapping("/recipes/{recipeId}")
    @Operation(summary = "Add or update a new ingredient to a recipe")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    public IngredientResponse addIngredient(@PathVariable Long recipeId, @RequestBody IngredientRequest request) {
        if (request.name == null || request.name.isEmpty()) {
            throw RestError.MISSING_FIELD.get("name");
        }

        if (request.quantity == null || request.quantity.isNaN() || request.quantity <= 0) {
            throw RestError.MISSING_FIELD.get("quantity");
        }

        if (request.unit == null || request.unit.isEmpty()) {
            throw RestError.MISSING_FIELD.get("unit");
        }

        return IngredientEntityToIngredientResponse.convert(ingredientService.addIngredient(getUserId(), recipeId, request.name, request.quantity, request.unit));
    }

    @DeleteMapping("/recipes/{recipeId}/{ingredientName}")
    @Operation(summary = "Delete an ingredient from a recipe")
    @Parameter(name = "ingredientName", description = "The name of the ingredient")
    public ResponseEntity<Void> deleteIngredient(@PathVariable String ingredientName, @PathVariable Long recipeId) {
        ingredientService.deleteIngredient(getUserId(), recipeId, ingredientName);

        return ResponseEntity.noContent().build();
    }


}
