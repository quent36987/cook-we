package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.data.repository.RecipeStepRepository;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.RecipeStepEntity;
import com.cookwe.utils.converters.RecipeModelToRecipeEntity;
import lombok.Data;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeStepRepository recipeStepRepository;

    //    @Transactional
    public List<RecipeModel> getRecipes() {
        Iterable<RecipeModel> recipes = recipeRepository.findAll();

        List<RecipeModel> recipesList = new ArrayList<>();
        recipes.forEach(recipesList::add);
        return recipesList;
        //return RecipeModelToRecipeEntity.convertList(recipes);
    }

    @Transactional
    public Optional<RecipeModel> getRecipeById(Long id) {
        Optional<RecipeModel> optionalRecipe = recipeRepository.findById(id);
        optionalRecipe.ifPresent(recipe -> Hibernate.initialize(recipe.getSteps()));
        return optionalRecipe;
    }

    public RecipeModel createRecipe(String name, Long time, String season, List<String> steps) {
        RecipeModel recipe = new RecipeModel();
        recipe.setName(name);
        recipe.setTime(time);
        recipe.setCreatedAt(LocalDateTime.now());



        switch (season) {
            case "spring":
                recipe.setSeason(ESeason.SPRING);
                break;
            case "winter":
                recipe.setSeason(ESeason.WINTER);
                break;
            default:
                recipe.setSeason(ESeason.SUMMER);
                break;
        }

        RecipeModel savedRecipe = recipeRepository.save(recipe);

        for (String step : steps) {
            RecipeStepModel recipeStep = new RecipeStepModel(savedRecipe,step);
            recipeStepRepository.save(recipeStep);
        }


        return savedRecipe;
    }
}
