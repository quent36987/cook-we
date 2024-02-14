package com.cookwe.presentation.response;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.RecipeStepEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeResponse {
    public Long id;

    public String name;

    public Long time;

    public Long portions;

    public ESeason season;

    public UserResponse user;

    public LocalDateTime createdAt;

    public List<RecipeStepResponse> steps;

//    public List<UserResponse> favoritedBy;
}
