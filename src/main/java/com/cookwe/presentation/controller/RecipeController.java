package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.RecipeStepEntity;
import com.cookwe.domain.service.RecipeService;
import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.presentation.response.RecipeStepResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
import com.cookwe.utils.converters.RecipeStepEntityToRecipeStepResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipe", description = "Recipe operations")
public class RecipeController {


    @Autowired
    private RecipeService recipeService;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @GetMapping("")
    @Operation(summary = "Get all recipes")
    public List<RecipeResponse> getRecipes() {
        List<RecipeEntity> recipes = recipeService.getRecipes();

        return RecipeEntityToRecipeResponse.convertList(recipes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recipe by id")
    @Parameter(name = "id", description = "The id of the recipe")
    public RecipeResponse getRecipeById(@PathVariable Long id) {
        RecipeEntity recipe = recipeService.getRecipeEntityById(id);

        System.out.println("recipe: " + recipe);
        return RecipeEntityToRecipeResponse.convert(recipe);
    }

    @GetMapping("/{recipeId}/steps")
    @Operation(summary = "Get all steps of a recipe")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    public List<RecipeStepResponse> getStepsByRecipeId(@PathVariable Long recipeId) {
        Iterable<RecipeStepEntity> steps = recipeService.getStepsByRecipeId(recipeId);

        return RecipeStepEntityToRecipeStepResponse.convertList(steps);
    }

    @GetMapping("/ingredients/search")
    @Operation(summary = "Get all recipes with at most one of the given ingredients")
    @Parameter(name = "ingredients", description = "The ingredients to search for")
    public List<RecipeResponse> getRecipesByIngredients(@RequestParam List<String> ingredients) {
        List<RecipeEntity> recipes = recipeService.getRecipesByIngredients(ingredients);

        return RecipeEntityToRecipeResponse.convertList(recipes);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a recipe")
    public RecipeResponse createRecipe(@RequestBody CreateRecipeRequest request) {
        if (request.name == null || request.name.isEmpty()) {
            throw RestError.MISSING_FIELD.get("name");
        }

        if (request.time == null || request.time <= 0) {
            throw RestError.MISSING_FIELD.get("time");
        }

        if (request.portions == null || request.portions <= 0) {
            throw RestError.MISSING_FIELD.get("portions");
        }

        if (request.ingredients == null) {
            throw RestError.MISSING_FIELD.get("ingredients");
        }


        RecipeEntity savedRecipe = recipeService.createRecipe(
                this.getUserId(),
                request.name,
                request.time,
                request.portions,
                request.season,
                request.steps,
                request.ingredients
        );

        System.out.println("savedRecipe: " + savedRecipe);

        return RecipeEntityToRecipeResponse.convert(savedRecipe);
    }

    @PutMapping("/{recipeId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update a recipe")
    @Parameter(name = "id", description = "The id of the recipe")
    public RecipeResponse updateRecipe(@PathVariable Long recipeId, @RequestBody CreateRecipeRequest request) {
        if (request.name == null || request.name.isEmpty()) {
            throw RestError.MISSING_FIELD.get("name");
        }

        if (request.time == null || request.time <= 0) {
            throw RestError.MISSING_FIELD.get("time");
        }

        if (request.portions == null || request.portions <= 0) {
            throw RestError.MISSING_FIELD.get("portions");
        }

        if (request.ingredients == null) {
            throw RestError.MISSING_FIELD.get("ingredients");
        }

        RecipeEntity updatedRecipe = recipeService.updateRecipe(
                getUserId(),
                recipeId,
                request.name,
                request.time,
                request.portions,
                request.season,
                request.steps,
                request.ingredients
        );

        return RecipeEntityToRecipeResponse.convert(updatedRecipe);
    }

    @DeleteMapping("/{recipeId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a recipe")
    @Parameter(name = "id", description = "The id of the recipe")
    public String deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(getUserId(), recipeId);

        return "Recipe deleted";
    }
}
