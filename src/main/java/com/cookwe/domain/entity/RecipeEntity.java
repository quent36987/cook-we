package com.cookwe.domain.entity;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.UserModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeEntity {

    public Long id;

    public String name;

    public Long time;

    public ESeason season;

    public UserModel user;

    public LocalDateTime createdAt;

    public RecipeStepEntity[] steps;

    public UserModel[] favoritedBy;
}
