package com.cookwe.presentation.response;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.RecipeStepEntity;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class RecipeResponse {
    public Long id;

    public String name;

    public Long time;

    public ESeason season;

    public UserResponse user;

    public LocalDateTime createdAt;

    public List<RecipeStepResponse> steps;

    public List<UserResponse> favoritedBy;
}
