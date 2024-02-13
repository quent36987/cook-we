package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.IngredientEntity;
import com.cookwe.domain.service.IngredientService;
import com.cookwe.presentation.request.CreateIngredientRequest;
import com.cookwe.presentation.response.IngredientResponse;
import com.cookwe.utils.converters.IngredientEntityToIngredientResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
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

    /**
     * Get all ingredients
     *
     * @param recipeId - The id of the recipe
     * @return - An List object of Ingredient full filled
     */
    @GetMapping("/recipes/{recipeId}")
    public List<IngredientResponse> getIngredientsByRecipeId(@PathVariable Long recipeId) {
        List<IngredientEntity> ingredients = ingredientService.getIngredientsByRecipeId(recipeId);

        return IngredientEntityToIngredientResponse.convertList(ingredients);
    }

    /**
     * add a new ingredient to a recipe
     *
     * @param recipeId - The id of the recipe
     * @param request  - A CreateIngredientRequest object
     * @return - A Ingredient object
     */
    @PutMapping("/recipes/{recipeId}")
    public IngredientResponse addIngredient(@PathVariable Long recipeId, @RequestBody CreateIngredientRequest request) {
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

    /**
     * Delete an ingredient
     *
     * @param ingredientName - The name of the ingredient
     * @param recipeId - The id of the recipe
     */
    @DeleteMapping("/recipes/{recipeId}/{ingredientName}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable String ingredientName, @PathVariable Long recipeId) {
        ingredientService.deleteIngredient(getUserId(), recipeId, ingredientName);

        return ResponseEntity.noContent().build();
    }


}
