package com.cookwe.presentation.request;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.RecipeStepEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateRecipeRequest {
    public String name;

    public Long time;

    public String season;

    public List<String> steps;
}
