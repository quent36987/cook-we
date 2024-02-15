package com.cookwe.utils.converters;

import com.cookwe.domain.entity.RecipePictureEntity;
import com.cookwe.presentation.response.RecipePictureResponse;

import java.util.ArrayList;
import java.util.List;

public class RecipePictureEntityToRecipePictureReponse {
    public static RecipePictureResponse convert(RecipePictureEntity recipePictureEntity) {
        return new RecipePictureResponse()
                .withImageUrl(recipePictureEntity.getImageUrl());
    }

    public static List<RecipePictureResponse> convertList(Iterable<RecipePictureEntity> recipePictureEntities) {
        List<RecipePictureResponse> responses = new ArrayList<>();
        for (RecipePictureEntity recipePictureEntity : recipePictureEntities) {
            responses.add(convert(recipePictureEntity));
        }
        return responses;
    }
}
