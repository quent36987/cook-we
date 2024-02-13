package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.data.repository.RecipeStepRepository;
import com.cookwe.data.repository.UserRepository;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.RecipeStepEntity;
import com.cookwe.presentation.request.CreateIngredientRequest;
import com.cookwe.utils.converters.RecipeModelToRecipeEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Data
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeStepRepository recipeStepRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientService ingredientService;

    // @Transactional
    public List<RecipeEntity> getRecipes() {
        Iterable<RecipeModel> recipes = recipeRepository.findAll();

        //System.out.println("recipes: " + recipes.toString());

        return RecipeModelToRecipeEntity.convertList(recipes);
    }

    @Transactional
    public RecipeModel getRecipeModelById(Long id) {
        Optional<RecipeModel> optionalRecipe = recipeRepository.findById(id);

        if (optionalRecipe.isEmpty()) {
            throw RestError.RECIPE_NOT_FOUND.get(id);
        }

        return optionalRecipe.get();
    }

    @Transactional
    public RecipeEntity getRecipeById(Long id) {
        RecipeModel recipe = getRecipeModelById(id);

        // FIXME WHYYYY
//          List<UserModel> users = new ArrayList<>();
//          // get user who liked the recipe
//            recipe.getFavoritedBy().forEach(user -> {
//                UserModel userModel = userRepository.findById(user.getId()).get();
//                users.add(userModel);
//            });
//
//            recipe.setFavoritedBy(users);

        return RecipeModelToRecipeEntity.convert(recipe);
    }

    @Transactional
    public RecipeEntity createRecipe(Long UserId, String name, Long time, Long portions, String season, List<String> steps, List<CreateIngredientRequest> ingredientRequests) {
        RecipeModel recipe = new RecipeModel();
        recipe.setName(name);
        recipe.setTime(time);
        recipe.setPortions(portions);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUser(new UserModel(UserId));

        try {
            ESeason.valueOf(season);
        } catch (IllegalArgumentException e) {
            throw RestError.SEASON_NOT_FOUND.get(season);
        }

        recipe.setSeason(ESeason.valueOf(season));

        RecipeModel savedRecipe = recipeRepository.save(recipe);

        for (String step : steps) {
            RecipeStepModel recipeStep = new RecipeStepModel(savedRecipe, step);
            recipeStepRepository.save(recipeStep);
        }

        for (CreateIngredientRequest ingredientRequest : ingredientRequests) {
            ingredientService.addIngredientToModel(UserId, savedRecipe, ingredientRequest.name, ingredientRequest.quantity, ingredientRequest.unit);
        }

        return RecipeModelToRecipeEntity.convert(savedRecipe);
    }
}
