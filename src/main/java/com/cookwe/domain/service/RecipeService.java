package com.cookwe.domain.service;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.domain.entity.RecipeEntity;
import com.cookwe.utils.converters.RecipeModelToRecipeEntity;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

//    @Transactional
    public List<RecipeEntity> getRecipes() {
        Iterable<RecipeModel> recipes = recipeRepository.findAll();
        return RecipeModelToRecipeEntity.convertList(recipes);
    }
}
