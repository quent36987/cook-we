package com.cookwe.utils.converters;

import com.cookwe.domain.entity.CommentEntity;
import com.cookwe.presentation.response.CommentResponse;

import java.util.ArrayList;
import java.util.List;

public class CommentEntityToCommentResponse {
    public static CommentResponse convert(CommentEntity commentEntity) {
        return new CommentResponse().withText(commentEntity.getText())
                .withUser(UserEntityToUserResponse.convert(commentEntity.getUser()))
                .withRecipeId(commentEntity.getRecipeId())
                .withId(commentEntity.getId())
                .withCreatedAt(commentEntity.getCreatedAt());
    }

    public static List<CommentResponse> convertList(List<CommentEntity> commentEntities) {
        List<CommentResponse> responses = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntities) {
            responses.add(convert(commentEntity));
        }
        return responses;
    }
}
