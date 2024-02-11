package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.service.RecipeService;


import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
import com.cookwe.utils.errors.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {


    @Autowired
    private RecipeService recipeService;

    /**
     * Read - Get all recipes
     *
     * @return - An List object of Recipe full filled
     */
    @GetMapping("")
    public List<RecipeResponse> getRecipes() {
        List<RecipeEntity> recipes = recipeService.getRecipes();

        return RecipeEntityToRecipeResponse.convertList(recipes);
    }

    /**
     * Create - Add a new recipe
     *
     * @param request An object recipe (name, time)
     * @return - An object recipe full filled
     */
    @PostMapping("")
    public RecipeResponse createRecipe(@RequestBody CreateRecipeRequest request) {
        if (request.name == null || request.time == null || request.name.isEmpty() || request.time <= 0) {
            throw RestError.MISSING_FIELD.get("name or time");
        }

        RecipeEntity savedRecipe = recipeService.createRecipe(request.name, request.time);

        return RecipeEntityToRecipeResponse.convert(savedRecipe);
    }


}
