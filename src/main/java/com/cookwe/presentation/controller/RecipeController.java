package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.IngredientDTO;
import com.cookwe.domain.entity.RecipeDetailDTO;
import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.entity.RecipeStepDTO;
import com.cookwe.domain.service.RecipeService;
import com.cookwe.presentation.request.RecipeRequest;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.utils.converters.StringToESeason;
import com.cookwe.utils.converters.StringToEType;
import com.cookwe.utils.converters.StringToEUnit;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipe", description = "Recipe operations")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @GetMapping("")
    @Operation(summary = "Get all recipes")
    public List<RecipeDTO> getRecipes() {
        return recipeService.getRecipes();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recipe detail by id")
    @Parameter(name = "id", description = "The id of the recipe")
    public RecipeDetailDTO getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeDetailById(id);
    }

    @GetMapping("/ingredients/search")
    @Operation(summary = "Get all recipes with at most one of the given ingredients")
    @Parameter(name = "ingredients", description = "The ingredients to search for")
    public List<RecipeDTO> getRecipesByIngredients(@RequestParam List<String> ingredients) {
        return recipeService.getRecipesByIngredients(ingredients);
    }

    private void verifyCreateRecipeRequest(RecipeRequest request) {
        if (request == null)
            throw RestError.MISSING_FIELD.get("request");

        if (request.name == null || request.name.isEmpty()) {
            throw RestError.MISSING_FIELD.get("name");
        }

        if (request.time == null || request.time <= 0) {
            throw RestError.MISSING_FIELD.get("time");
        }

        if (request.portions == null || request.portions <= 0) {
            throw RestError.MISSING_FIELD.get("portions");
        }

        if (request.season == null || request.season.isEmpty()) {
            throw RestError.MISSING_FIELD.get("season");
        }

        if (request.type == null || request.type.isEmpty()) {
            throw RestError.MISSING_FIELD.get("type");
        }

        if (request.ingredients == null) {
            throw RestError.MISSING_FIELD.get("ingredients");
        }

        if (request.steps == null) {
            throw RestError.MISSING_FIELD.get("steps");
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a recipe")
    public RecipeDTO createRecipe(@RequestBody RecipeRequest request) {
        verifyCreateRecipeRequest(request);

        // FIXME ? maybe 3 sub function ?

        RecipeDetailDTO recipe = new RecipeDetailDTO();
        recipe.setName(request.name);
        recipe.setTime(request.time);
        recipe.setPortions(request.portions);
        recipe.setSeason(StringToESeason.convert(request.season));
        recipe.setSteps(request.steps.stream().map(step -> {
            RecipeStepDTO stepDTO = new RecipeStepDTO();
            stepDTO.setText(step);
            return stepDTO;
        }).toList());
        recipe.setType(StringToEType.convert(request.type));
        recipe.setIngredients(request.ingredients.stream().map(ingredient -> {
            IngredientDTO ingredredient = new IngredientDTO();
            ingredredient.setName(ingredient.name);
            ingredredient.setQuantity(ingredient.quantity);
            ingredredient.setUnit(StringToEUnit.convert(ingredient.unit));
            return ingredredient;
        }).toList());
        recipe.setOwnerId(getUserId());
        recipe.setName(request.name);

        return recipeService.createRecipe(recipe);
    }

    @PutMapping("/{recipeId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update a recipe")
    @Parameter(name = "id", description = "The id of the recipe")
    public RecipeDTO updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeRequest request) {
        verifyCreateRecipeRequest(request);

        //FIXME: send a different props to updateRecipe (to many arguments)
        return recipeService.updateRecipe(
                getUserId(),
                recipeId,
                request.name,
                request.time,
                request.portions,
                request.season,
                request.steps,
                request.ingredients,
                request.type
        );
    }

    @DeleteMapping("/{recipeId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a recipe")
    @Parameter(name = "id", description = "The id of the recipe")
    public MessageResponse deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(getUserId(), recipeId);

        return new MessageResponse("Recipe deleted successfully");
    }
}
