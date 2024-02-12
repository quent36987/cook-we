package com.cookwe.presentation.request;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.RecipeStepEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class CreateRecipeRequest {
    public String name;

    public Long time;

    public String season;

    public List<String> steps;
}
