package com.cookwe.presentation.response;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.EType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@With
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class RecipeDetailResponse extends RecipeResponse {
    public List<IngredientResponse> ingredients;
    public List<CommentResponse> comments;
}
