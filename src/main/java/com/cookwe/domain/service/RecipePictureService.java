package com.cookwe.domain.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.RecipePictureModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.RecipePictureRepository;
import com.cookwe.domain.entity.RecipePictureEntity;
import com.cookwe.utils.converters.RecipePictureModelToRecipePictureEntity;
import com.cookwe.utils.errors.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Service
@Data
public class RecipePictureService {
    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipePictureRepository recipePictureRepository;

    private final Path root = Paths.get("uploads");

    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw RestError.INTERNAL_SERVER_ERROR.get();
        }
    }

    @Transactional
    public List<RecipePictureEntity> getRecipePicturesByRecipeId(Long recipeId) {
        Iterable<RecipePictureModel> pictures = recipePictureRepository.findByRecipeId(recipeId);

        return RecipePictureModelToRecipePictureEntity.convertList(pictures);
    }

    @Transactional
    public RecipePictureEntity save(Long userId, Long recipeId, MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw RestError.MISSING_FIELD.get("file");
        }

        RecipeModel recipeModel = recipeService.getRecipeModelById(recipeId);

        try {
            String fileName = "i" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(fileName));

            RecipePictureModel recipePictureModel = new RecipePictureModel()
                    .withRecipe(recipeModel)
                    .withImageUrl(fileName)
                    .withCreatedAt(LocalDateTime.now())
                    .withUser(new UserModel().withId(userId));

            recipePictureModel = recipePictureRepository.save(recipePictureModel);

            return RecipePictureModelToRecipePictureEntity.convert(recipePictureModel);
        } catch (Exception e) {

            throw RestError.FILE_CANT_BE_SAVE.get();
        }
    }

    @Transactional
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw RestError.PICTURE_NOT_FOUND.get();
            }
        } catch (MalformedURLException e) {
            throw RestError.PICTURE_NOT_FOUND.get();
        }
    }

    @Transactional
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Transactional
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw RestError.PICTURE_NOT_FOUND.get();
        }
    }

    @Transactional
    public void delete(Long userId, String filename) {
        Optional<RecipePictureModel> recipePictureModel = recipePictureRepository.findByImageUrl(filename);

        if (recipePictureModel.isEmpty()) {
            throw RestError.PICTURE_NOT_FOUND.get();
        }

        if (!recipePictureModel.get().getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of this picture");
        }

        try {
            Files.deleteIfExists(root.resolve(filename));
            recipePictureRepository.delete(recipePictureModel.get());
        } catch (IOException e) {
            throw RestError.FILE_CANT_BE_SAVE.get();
        }
    }
}
