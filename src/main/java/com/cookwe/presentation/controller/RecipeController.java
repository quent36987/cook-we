package com.cookwe.presentation.controller;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.service.RecipeService;


import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
import com.cookwe.utils.errors.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public List<RecipeModel> getRecipes() {
        return recipeService.getRecipes();
        //  List<RecipeEntity> recipes = recipeService.getRecipes();

        //return RecipeEntityToRecipeResponse.convertList(recipes);
    }

    @GetMapping("/{id}")
    public RecipeModel getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id).orElseThrow(() -> RestError.POST_NOT_FOUND.get(id));
    }

    /**
     * Create - Add a new recipe
     *
     * @param request An object recipe (name, time)
     * @return - An object recipe full filled
     */
    @PostMapping("")
    //@PreAuthorize("hasRole('USER')")
    public RecipeModel createRecipe(@RequestBody CreateRecipeRequest request) {
        if (request.name == null || request.time == null || request.name.isEmpty() || request.time <= 0) {
            throw RestError.MISSING_FIELD.get("name or time");
        }

        System.out.println("request.name: " + request.name);

        RecipeModel savedRecipe = recipeService.createRecipe(request.name, request.time, request.season, request.steps);

        return savedRecipe;
        //return RecipeEntityToRecipeResponse.convert(savedRecipe);
    }


}
