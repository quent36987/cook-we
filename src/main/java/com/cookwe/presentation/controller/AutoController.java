package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeDetailDTO;
import com.cookwe.domain.service.AutoService;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auto")
@Tag(name = "Auto", description = "Auto IA controller (beta)")
public class AutoController {

    @Autowired
    private AutoService autoService;

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @PostMapping("/generate-recipe")
    @Operation(summary = "Upload a picture of a recipe and get the RecipeResponse")
    @Parameter(name = "file", description = "picture of ")
    //@PreAuthorize("hasRole('ADMIN')")
    public RecipeDetailDTO generateRecipeEntityWithPicture(@RequestParam("file") MultipartFile file) throws IOException {
        RecipeDetailDTO recipe = autoService.generateRecipeEntityWithPicture(file, getUserId());

        return recipe;
    }

    @PostMapping("/generate-recipe-url")
    @Operation(summary = "Upload a url of a recipe and get the RecipeResponse")
    public RecipeDetailDTO generateRecipeEntityWithUrl(@RequestBody String url) throws JsonProcessingException {
        RecipeDetailDTO recipe = autoService.generateRecipeEntityWithUrl(url, getUserId());

        return recipe;
    }
}
