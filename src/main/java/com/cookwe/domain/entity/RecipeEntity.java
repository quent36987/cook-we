package com.cookwe.domain.entity;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.UserModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeEntity {
    public Long id;

    public String name;

    public Long time;

    public Long portions;

    public ESeason season;

    public UserEntity user;

    public LocalDateTime createdAt;

    public List<RecipeStepEntity> steps;

    public List<UserEntity> favoritedBy;
}
