package com.cookwe.presentation.controller;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.service.RecipeService;


import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
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

    @GetMapping("/{id}")
    public RecipeResponse getRecipeById(@PathVariable Long id) {
        RecipeEntity recipe = recipeService.getRecipeById(id);

        System.out.println("recipe: " + recipe);
        return RecipeEntityToRecipeResponse.convert(recipe);
    }

    /**
     * Create - Add a new recipe
     *
     * @param request An object recipe (name, time)
     * @return - An object recipe full filled
     */
    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public RecipeResponse createRecipe(@RequestBody CreateRecipeRequest request) {
//        if (request == null) {
//            throw RestError.MISSING_FIELD.get("request");
//        } //FIXME need ?

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
