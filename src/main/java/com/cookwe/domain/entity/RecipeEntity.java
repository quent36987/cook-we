package com.cookwe.domain.entity;

import com.cookwe.data.model.ESeason;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeEntity {
    private Long id;
    private String name;
    private Long time;
    private Long portions;
    private ESeason season;
    private UserEntity user;
    private LocalDateTime createdAt;
    private List<RecipeStepEntity> steps;
    private List<RecipePictureEntity> pictures;
}
