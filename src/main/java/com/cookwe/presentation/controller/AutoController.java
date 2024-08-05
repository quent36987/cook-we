package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipeDetailDTO;
import com.cookwe.domain.service.AutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auto")
@Tag(name = "Auto", description = "Auto IA controller (beta)")
public class AutoController {

    @Autowired
    private AutoService autoService;

    @PostMapping("/generate-recipe")
    @Operation(summary = "Upload a picture of a recipe and get the RecipeResponse")
    @Parameter(name = "file", description = "picture of ")
    //@PreAuthorize("hasRole('ADMIN')")
    public RecipeDetailDTO generateRecipeEntityWithPicture(@RequestParam("file") MultipartFile file) throws IOException {
        RecipeDetailDTO recipe = autoService.generateRecipeEntityWithPicture(file);

        return recipe;
    }
}
