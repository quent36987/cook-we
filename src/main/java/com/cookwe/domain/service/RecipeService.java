package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.*;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.RecipeStepEntity;
import com.cookwe.presentation.request.CreateIngredientRequest;
import com.cookwe.utils.converters.RecipeModelToRecipeEntity;
import com.cookwe.utils.converters.RecipeStepModelToRecipeStepEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
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
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipePictureRepository recipePictureRepository;

    @Transactional
    public List<RecipeEntity> getRecipes() {
        Iterable<RecipeModel> recipes = recipeRepository.findAll();

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
    public RecipeEntity getRecipeEntityById(Long id) {
        RecipeModel recipe = getRecipeModelById(id);

        return RecipeModelToRecipeEntity.convert(recipe);
    }

    @Transactional
    public List<RecipeStepEntity> getStepsByRecipeId(Long recipeId) {
        Iterable<RecipeStepModel> steps = recipeStepRepository.findByRecipeId(recipeId);

        return RecipeStepModelToRecipeStepEntity.convertList(steps);
    }

    @Transactional
    public ESeason getSeasonByString(String season) {
        try {
            return ESeason.valueOf(season);
        } catch (IllegalArgumentException e) {
            throw RestError.SEASON_NOT_FOUND.get(season);
        }
    }

    @Transactional
    public void addIngredientToModel(List<CreateIngredientRequest> ingredientRequests, RecipeModel recipe, Long userId) {
        for (CreateIngredientRequest ingredientRequest : ingredientRequests) {
            ingredientService.addIngredientToModel(userId, recipe, ingredientRequest.name, ingredientRequest.quantity, ingredientRequest.unit);
        }
    }

    @Transactional
    public void addStepToModel(List<String> steps, RecipeModel recipe) {
        Long index = 0L;
        for (String step : steps) {
            RecipeStepModel recipeStep = new RecipeStepModel(recipe, step, index);
            recipeStepRepository.save(recipeStep);
            index++;
        }
    }

    @Transactional
    public RecipeEntity createRecipe(Long UserId, String name, Long time, Long portions, String season, List<String> steps, List<CreateIngredientRequest> ingredientRequests) {
        RecipeModel recipe = new RecipeModel();
        recipe.setName(name);
        recipe.setTime(time);
        recipe.setPortions(portions);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUser(new UserModel(UserId));
        recipe.setSeason(getSeasonByString(season));

        RecipeModel savedRecipe = recipeRepository.save(recipe);

        addStepToModel(steps, savedRecipe);

        addIngredientToModel(ingredientRequests, savedRecipe, UserId);

        return RecipeModelToRecipeEntity.convert(savedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long userId, Long recipeId) {
        RecipeModel recipe = getRecipeModelById(recipeId);

        if (!recipe.getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of this recipe");
        }

        recipeRepository.delete(recipe);
    }

    @Transactional
    public RecipeEntity updateRecipe(Long userId, Long recipeId, String name, Long time, Long portions, String season, List<String> steps, List<CreateIngredientRequest> ingredientRequests) {
        RecipeModel recipe = getRecipeModelById(recipeId);

        if (!recipe.getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of this recipe");
        }

        recipe.setName(name);
        recipe.setTime(time);
        recipe.setPortions(portions);
        recipe.setSeason(getSeasonByString(season));

        Iterable<RecipeStepModel> recipeSteps = recipeStepRepository.findByRecipeId(recipeId);

        recipeStepRepository.deleteAll(recipeSteps);

        addStepToModel(steps, recipe);

        Iterable<IngredientModel> ingredients = ingredientRepository.findByRecipeId(recipeId);

        ingredientRepository.deleteAll(ingredients);

        addIngredientToModel(ingredientRequests, recipe, userId);

        recipeRepository.save(recipe);

        return RecipeModelToRecipeEntity.convert(recipe);
    }
}
