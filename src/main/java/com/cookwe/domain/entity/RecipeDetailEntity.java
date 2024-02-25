package com.cookwe.domain.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
@SuperBuilder
public class RecipeDetailEntity extends RecipeEntity {
    private List<IngredientEntity> ingredients;
    private List<CommentEntity> comments;
    private boolean isFavorite;
}
