package com.cookwe.domain.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeStepEntity {
    private Long id;
    private Long recipeId;
    private Long stepNumber;
    private String text;
}