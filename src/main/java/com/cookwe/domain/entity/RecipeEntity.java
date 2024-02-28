package com.cookwe.domain.entity;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.EType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RecipeEntity {
    private Long id;
    private String name;
    private Long time;
    private Long portions;
    private ESeason season;
    private EType type;
    private UserEntity user;
    private Timestamp createdAt;
    private List<RecipeStepEntity> steps;
    private List<RecipePictureEntity> pictures;
}
