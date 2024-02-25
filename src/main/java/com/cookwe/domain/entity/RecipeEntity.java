package com.cookwe.domain.entity;

import com.cookwe.data.model.ESeason;
import com.cookwe.data.model.EType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private List<RecipeStepEntity> steps;
    private List<RecipePictureEntity> pictures;
}
