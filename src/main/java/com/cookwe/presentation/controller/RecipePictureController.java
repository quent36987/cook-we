package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RecipePictureEntity;
import com.cookwe.domain.service.RecipePictureService;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.presentation.response.RecipePictureResponse;
import com.cookwe.utils.converters.RecipePictureEntityToRecipePictureReponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pictures")
@Tag(name = "Recipe Picture", description = "Recipe Picture operations")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class RecipePictureController {
    @Autowired
    private RecipePictureService recipePictureService;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @PostMapping("/recipes/{recipeId}")
    @Operation(summary = "Upload a picture for a recipe")
    @Parameter(name = "recipeId", description = "Id of the recipe")
    @PreAuthorize("hasRole('USER')")
    public RecipePictureResponse uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long recipeId) {
        RecipePictureEntity recipePicture = recipePictureService.save(getUserId(), recipeId, file);

        return RecipePictureEntityToRecipePictureReponse.convert(recipePicture);
    }

    @GetMapping("/recipes/{recipeId}")
    @Operation(summary = "Get pictures for a recipe")
    @Parameter(name = "recipeId", description = "Id of the recipe")
    public List<RecipePictureResponse> getRecipePictures(@PathVariable Long recipeId) {
        List<RecipePictureEntity> recipePictures = recipePictureService.getRecipePicturesByRecipeId(recipeId);

        return RecipePictureEntityToRecipePictureReponse.convertList(recipePictures);
    }

    @GetMapping("/{filename:.+}")
    @Operation(summary = "Download a picture")
    @Parameter(name = "filename", description = "Name of the picture")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = recipePictureService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/{filename:.+}")
    @Operation(summary = "Delete a picture")
    @Parameter(name = "filename", description = "Name of the picture")
    public MessageResponse deleteFile(@PathVariable String filename) {
        recipePictureService.delete(getUserId(), filename);

        return new MessageResponse("Picture deleted successfully");
    }
}