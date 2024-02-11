package com.cookwe.presentation;

import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.service.RecipeService;


import com.cookwe.presentation.response.RecipeResponse;
import com.cookwe.utils.converters.RecipeEntityToRecipeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {


    @Autowired
    private RecipeService recipeService;

    @GetMapping("")
    public List<RecipeResponse> getRecipes() {
        List<RecipeEntity> recipes = recipeService.getRecipes();

        return RecipeEntityToRecipeResponse.convertList(recipes);
    }

}
