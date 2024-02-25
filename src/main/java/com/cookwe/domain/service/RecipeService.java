package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.*;
import com.cookwe.data.repository.interfaces.*;
import com.cookwe.domain.entity.RecipeDetailEntity;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.RecipeStepEntity;
import com.cookwe.presentation.request.IngredientRequest;
import com.cookwe.utils.converters.*;
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
    private RecipeRepositoryCustom recipeRepositoryCustom;

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

    @Autowired
    private CommentService commentService;

    @Autowired
    private RecipePictureService recipePictureService;

    @Transactional
    public List<RecipeEntity> getRecipes() {
        Iterable<RecipeModel> recipes = recipeRepository.findAll();

        return RecipeModelToRecipeEntity.convertList(recipes);
    }

    @Transactional
    public RecipeDetailEntity getRecipeDetailById(Long recipeId) {
        RecipeModel recipeModel = recipeRepositoryCustom.getRecipeModelById(recipeId);

        RecipeDetailEntity recipe = RecipeModelToRecipeDetailEntity.convert(recipeModel);

        recipe.setIngredients(ingredientService.getIngredientsByRecipeId(recipeId));

        recipe.setComments(commentService.getCommentsByRecipeId(recipeId));

        //recipe.setPictures(recipePictureService.getRecipePicturesByRecipeId(recipeId));

        return recipe;
    }

    @Transactional
    public List<RecipeStepEntity> getStepsByRecipeId(Long recipeId) {
        Iterable<RecipeStepModel> steps = recipeStepRepository.findByRecipeId(recipeId);

        return RecipeStepModelToRecipeStepEntity.convertList(steps);
    }

    @Transactional
    public List<RecipeEntity> getRecipesByUserId(Long userId) {
        List<RecipeModel> recipes = recipeRepository.findByUserId(userId);

        return RecipeModelToRecipeEntity.convertList(recipes);
    }

    @Transactional
    public List<RecipeEntity> getRecipesByIngredients(List<String> ingredients) {
        Iterable<RecipeModel> recipeModels = recipeRepository.findByRecipeByIngredientsName(ingredients);

        return RecipeModelToRecipeEntity.convertList(recipeModels);
    }

    public void addIngredientToModel(List<IngredientRequest> ingredientRequests, RecipeModel recipe, Long userId) {
        for (IngredientRequest ingredientRequest : ingredientRequests) {
            ingredientService.addIngredientToModel(userId, recipe, ingredientRequest.name, ingredientRequest.quantity, ingredientRequest.unit);
        }
    }

    public void addStepToModel(List<String> steps, RecipeModel recipe) {
        Long index = 0L;
        for (String step : steps) {
            RecipeStepModel recipeStep = new RecipeStepModel(recipe, step, index);
            recipeStepRepository.save(recipeStep);
            index++;
        }
    }

    @Transactional
    public RecipeEntity createRecipe(Long userId, String name, Long time, Long portions, String season, List<String> steps, List<IngredientRequest> ingredientRequests, String type) {
        RecipeModel recipe = new RecipeModel();
        recipe.setName(name);
        recipe.setTime(time);
        recipe.setPortions(portions);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUser(new UserModel(userId));
        recipe.setSeason(StringToESeason.convert(season));
        recipe.setType(StringToEType.convert(type));

        RecipeModel savedRecipe = recipeRepository.save(recipe);

        addStepToModel(steps, savedRecipe);

        addIngredientToModel(ingredientRequests, savedRecipe, userId);

        return RecipeModelToRecipeEntity.convert(savedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long userId, Long recipeId) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        if (!recipe.getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of this recipe");
        }

        recipeRepository.delete(recipe);
    }

    @Transactional
    public RecipeEntity updateRecipe(Long userId, Long recipeId, String name, Long time, Long portions, String season, List<String> steps, List<IngredientRequest> ingredientRequests, String type){
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        if (!recipe.getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of this recipe");
        }

        recipe.setName(name);
        recipe.setTime(time);
        recipe.setPortions(portions);
        recipe.setSeason(StringToESeason.convert(season));
        recipe.setType(StringToEType.convert(type));

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
