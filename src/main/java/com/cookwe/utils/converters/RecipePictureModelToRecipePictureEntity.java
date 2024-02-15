package com.cookwe.utils.converters;

import com.cookwe.data.model.RecipePictureModel;
import com.cookwe.domain.entity.RecipePictureEntity;

import java.util.ArrayList;
import java.util.List;

public class RecipePictureModelToRecipePictureEntity {
    public static RecipePictureEntity convert(RecipePictureModel recipePictureModel) {
        return new RecipePictureEntity()
                .withId(recipePictureModel.getId())
                .withRecipe(RecipeModelToRecipeEntity.convert(recipePictureModel.getRecipe()))
                .withImageUrl(recipePictureModel.getImageUrl())
                .withUser(UserModelToUserEntity.convert(recipePictureModel.getUser()))
                .withCreatedAt(recipePictureModel.getCreatedAt());
    }

    public static List<RecipePictureEntity> convertList(Iterable<RecipePictureModel> recipePictureModels) {
        List<RecipePictureEntity> entities = new ArrayList<>();
        for (RecipePictureModel recipePictureModel : recipePictureModels) {
            entities.add(convert(recipePictureModel));
        }
        return entities;
    }
}
