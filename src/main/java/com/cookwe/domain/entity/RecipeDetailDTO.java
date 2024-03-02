package com.cookwe.domain.entity;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.EType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeDetailDTO {
    private Long id;
    private String name;
    private Long time;
    private Long portions;
    private ESeason season;
    private EType type;
    private String ownerUsername;
    private Long ownerId;
    private Timestamp createdAt;
    private List<RecipeStepDTO> steps;
    private List<RecipePictureDTO> pictures;
    private List<IngredientDTO> ingredients;
    private List<CommentDTO> comments;
}
