package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.data.repository.RecipeStepRepository;
import com.cookwe.data.repository.UserRepository;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.domain.entity.RecipeStepEntity;
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

    // @Transactional
    public List<RecipeEntity> getRecipes() {
        Iterable<RecipeModel> recipes = recipeRepository.findAll();

        //System.out.println("recipes: " + recipes.toString());

        return RecipeModelToRecipeEntity.convertList(recipes);
    }

    @Transactional
    public RecipeEntity getRecipeById(Long id) {
        Optional<RecipeModel> optionalRecipe = recipeRepository.findById(id);

        if (optionalRecipe.isEmpty()) {
            throw RestError.RECIPE_NOT_FOUND.get(id);
        }

        RecipeModel recipe = optionalRecipe.get();

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

    public RecipeEntity createRecipe(Long UserId, String name, Long time, String season, List<String> steps) {
        RecipeModel recipe = new RecipeModel();
        recipe.setName(name);
        recipe.setTime(time);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUser(new UserModel(UserId));

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
            RecipeStepModel recipeStep = new RecipeStepModel(savedRecipe, step);
            recipeStepRepository.save(recipeStep);
        }


        return RecipeModelToRecipeEntity.convert(savedRecipe);
    }
}
