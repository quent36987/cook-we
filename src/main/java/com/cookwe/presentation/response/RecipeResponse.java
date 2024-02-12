package com.cookwe.presentation.response;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.RecipeStepEntity;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class RecipeResponse {
    public Long id;

    public String name;

    public Long time;

    public ESeason season;

    public UserModel user;

    public LocalDateTime createdAt;

    public RecipeStepEntity[] steps;

    public UserModel[] favoritedBy;
}
