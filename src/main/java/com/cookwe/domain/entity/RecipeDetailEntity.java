package com.cookwe.domain.entity;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.EType;
import com.cookwe.presentation.response.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeDetailEntity {
    private Long id;
    private String name;
    private Long time;
    private Long portions;
    private ESeason season;
    private EType type;
    private UserEntity user;
    private LocalDateTime createdAt;
    private List<RecipeStepEntity> steps;
    private List<RecipePictureEntity> pictures;
    private List<IngredientEntity> ingredients;
    private List<CommentEntity> comments;
    private boolean isFavorite;
}
