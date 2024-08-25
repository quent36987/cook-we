package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.*;
import com.cookwe.data.repository.interfaces.*;
import com.cookwe.domain.entity.PageDTO;
import com.cookwe.domain.entity.RecipeDetailDTO;
import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.mapper.RecipeDetailMapper;
import com.cookwe.domain.mapper.RecipeMapper;
import com.cookwe.presentation.request.IngredientRequest;
import com.cookwe.utils.converters.*;
import com.cookwe.utils.errors.RestError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeRepositoryCustom recipeRepositoryCustom;
    private final RecipeStepRepository recipeStepRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientService ingredientService;
    private final RecipeMapper recipeMapper;
    private final RecipeDetailMapper recipeDetailMapper;

    public RecipeService(RecipeDetailMapper recipeDetailMapper, RecipeMapper recipeMapper, RecipeRepository recipeRepository, RecipeRepositoryCustom recipeRepositoryCustom, RecipeStepRepository recipeStepRepository, IngredientRepository ingredientRepository, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.recipeRepositoryCustom = recipeRepositoryCustom;
        this.recipeStepRepository = recipeStepRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientService = ingredientService;
        this.recipeMapper = recipeMapper;
        this.recipeDetailMapper = recipeDetailMapper;
    }

    @Transactional(readOnly = true)
    public PageDTO<RecipeDTO> getRecipes(Specification specification, Pageable pageable) {
        PageDTO<RecipeDTO> page = new PageDTO<>();
        page.setPage(pageable.getPageNumber());
        page.setSize(pageable.getPageSize());
        page.setTotalElements(recipeRepository.count(specification));

        Page<RecipeModel> recipePage = recipeRepository.findAll(specification, pageable);

        List<RecipeDTO> recipeDTOs = recipePage.getContent().stream()
                .map(recipeMapper::toDTO)
                .toList();

        page.setContent(recipeDTOs);

        return page;
    }

    @Transactional(readOnly = true)
    public RecipeDetailDTO getRecipeDetailById(Long recipeId) {
        RecipeModel recipeModel = recipeRepositoryCustom.getRecipeDetailById(recipeId);

        return recipeDetailMapper.toDTO(recipeModel);
    }

    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByUserId(Long userId) {
        List<RecipeModel> recipes = recipeRepository.findByUserId(userId);

        return recipeMapper.toDTOList(recipes);
    }

    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByIngredients(List<String> ingredients) {
        List<RecipeModel> recipeModels = recipeRepository.findByRecipeByIngredientsName(ingredients);

        return recipeMapper.toDTOList(recipeModels);
    }

    private void addIngredientToModel(List<IngredientRequest> ingredientRequests, RecipeModel recipe, Long userId) {
        for (IngredientRequest ingredientRequest : ingredientRequests) {
            ingredientService.addIngredientToModel(userId, recipe, ingredientRequest.name, ingredientRequest.quantity, ingredientRequest.unit);
        }
    }

    private void addStepToModel(List<String> steps, RecipeModel recipe) {
        Long index = 0L;
        for (String step : steps) {
            RecipeStepModel recipeStep = new RecipeStepModel(recipe, step, index);
            recipeStepRepository.save(recipeStep);
            index++;
        }
    }

    public RecipeDTO createRecipe(RecipeDetailDTO recipe) {
        RecipeModel recipeModel = recipeDetailMapper.toModel(recipe);

        for (RecipeStepModel step : recipeModel.getSteps()) {
            step.setRecipe(recipeModel);
        }

        for (IngredientModel ingredient : recipeModel.getIngredients()) {
            ingredient.setRecipe(recipeModel);
        }

        RecipeModel savedRecipe = recipeRepository.save(recipeModel);

        return recipeMapper.toDTO(savedRecipe);
    }

    public void deleteRecipe(Long userId, Long recipeId) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeDetailById(recipeId);

        if (!recipe.getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("Vous n'êtes pas autorisé à supprimer cette recette");
        }

        recipeRepository.delete(recipe);
    }

    public RecipeDTO updateRecipe(Long userId, Long recipeId, String name, Long time, Long portions, String season, List<String> steps, List<IngredientRequest> ingredientRequests, String type) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        if (!recipe.getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("Vous n'êtes pas autorisé à modifier cette recette");
        }

        recipe.setName(name);
        recipe.setTime(time);
        recipe.setPortions(portions);
        recipe.setSeason(StringToESeason.convert(season));
        recipe.setType(StringToEType.convert(type));

        List<RecipeStepModel> recipeSteps = recipeStepRepository.findByRecipeId(recipeId);

        recipeStepRepository.deleteAll(recipeSteps);

        addStepToModel(steps, recipe);

        Iterable<IngredientModel> ingredients = ingredientRepository.findByRecipeId(recipeId);

        ingredientRepository.deleteAll(ingredients);

        addIngredientToModel(ingredientRequests, recipe, userId);

        recipeRepository.save(recipe);

        return recipeMapper.toDTO(recipe);
    }
}
