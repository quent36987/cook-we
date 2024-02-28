package com.cookwe.presentation.response;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.EType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@With
@SuperBuilder
public class RecipeResponse {
    public Long id;

    public String name;

    public Long time;

    public Long portions;

    public ESeason season;
    public EType type;

    public UserResponse user;

    public Timestamp createdAt;

    public List<RecipeStepResponse> steps;

    public List<RecipePictureResponse> pictures;
}
