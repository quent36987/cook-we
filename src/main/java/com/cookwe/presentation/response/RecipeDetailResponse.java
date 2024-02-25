package com.cookwe.presentation.response;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.EType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@With
@AllArgsConstructor
@Getter
@Setter
public class RecipeDetailResponse {
    public Long id;
    public String name;
    public Long time;
    public Long portions;
    public ESeason season;
    public EType type;
    public UserResponse user;
    public LocalDateTime createdAt;
    public List<RecipeStepResponse> steps;
    public List<RecipePictureResponse> pictures;
    public List<IngredientResponse> ingredients;
    public List<CommentResponse> comments;
}
