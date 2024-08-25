package com.cookwe.domain.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.RecipePictureModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.RecipeRepositoryCustom;
import com.cookwe.data.repository.interfaces.RecipePictureRepository;
import com.cookwe.domain.entity.RecipePictureDTO;
import com.cookwe.domain.mapper.RecipePictureMapper;
import com.cookwe.utils.errors.RestError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;


import static com.cookwe.utils.image.CompressionUtils.compressByteImage;

@Service
@Slf4j
@Transactional
public class RecipePictureService {

    private final RecipePictureMapper pictureMapper;
    private final RecipeRepositoryCustom recipeRepositoryCustom;
    private final RecipePictureRepository recipePictureRepository;

    @Value("${cook-we.picture.path}")
    private String picturePath;

    public RecipePictureService(RecipePictureMapper pictureMapper, RecipeRepositoryCustom recipeRepositoryCustom, RecipePictureRepository recipePictureRepository) {
        this.pictureMapper = pictureMapper;
        this.recipeRepositoryCustom = recipeRepositoryCustom;
        this.recipePictureRepository = recipePictureRepository;
    }

    @Transactional(readOnly = true)
    public List<RecipePictureDTO> getRecipePicturesByRecipeId(Long recipeId) {
        List<RecipePictureModel> pictures = recipePictureRepository.findByRecipeId(recipeId);

        return pictureMapper.toDTOList(pictures);
    }

    public RecipePictureDTO save(Long userId, Long recipeId, MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw RestError.MISSING_FIELD.get("file");
        }

        RecipeModel recipeModel = recipeRepositoryCustom.getRecipeModelById(recipeId);

        Path root = Paths.get(picturePath);

        try {
            byte[] compressedBytes = compressByteImage(file.getBytes());
            String fileName = "i" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = root.resolve(fileName);
            Files.write(filePath, compressedBytes);

            RecipePictureModel recipePictureModel = RecipePictureModel.builder()
                    .recipe(recipeModel)
                    .imageUrl(fileName)
                    .user(new UserModel(userId)).build();

            recipePictureModel = recipePictureRepository.save(recipePictureModel);

            return pictureMapper.toDTO(recipePictureModel);
        } catch (Exception e) {
            throw RestError.FILE_CANT_BE_SAVE.get();
        }
    }

    public Resource load(String filename) {
        Path root = Paths.get(picturePath);
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

    public Stream<Path> loadAll() {
        Path root = Paths.get(picturePath);
        try {
            return Files.walk(root, 1).filter(path -> !path.equals(root)).map(root::relativize);
        } catch (IOException e) {
            throw RestError.PICTURE_NOT_FOUND.get();
        }
    }

    public void deleteAll() {
        Path root = Paths.get(picturePath);
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    public void delete(Long userId, String filename) {
        Path root = Paths.get(picturePath);
        Optional<RecipePictureModel> recipePictureModel = recipePictureRepository.findByImageUrl(filename);

        if (recipePictureModel.isEmpty()) {
            throw RestError.PICTURE_NOT_FOUND.get();
        }

        if (!recipePictureModel.get().getUser().getId().equals(userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("Vous n'êtes pas autorisé à supprimer cette image");
        }

        try {
            Files.deleteIfExists(root.resolve(filename));
            recipePictureRepository.delete(recipePictureModel.get());
        } catch (IOException e) {
            throw RestError.FILE_CANT_BE_SAVE.get();
        }
    }
}
