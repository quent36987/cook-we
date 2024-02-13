package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.service.RecipeService;
import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
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
        RecipeEntity recipe = recipeService.getRecipeById(id);

        System.out.println("recipe: " + recipe);
        return RecipeEntityToRecipeResponse.convert(recipe);
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
}
