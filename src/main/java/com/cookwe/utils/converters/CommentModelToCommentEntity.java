package com.cookwe.utils.converters;

import com.cookwe.data.model.CommentModel;
import com.cookwe.domain.entity.CommentEntity;

import java.util.ArrayList;
import java.util.List;

public class CommentModelToCommentEntity {
    public static CommentEntity convert(CommentModel commentModel) {
        return new CommentEntity().withId(commentModel.getId())
                .withText(commentModel.getText())
                .withCreatedAt(commentModel.getCreatedAt())
                .withRecipeId(commentModel.getRecipe().getId())
                .withUser(UserModelToUserEntity.convert(commentModel.getUser()));
    }

    public static List<CommentEntity> convertList(Iterable<CommentModel> commentModels) {
        List<CommentEntity> responses = new ArrayList<>();
        for (CommentModel commentModel : commentModels) {
            responses.add(convert(commentModel));
        }
        return responses;
    }
}
