package com.cookwe.domain.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeStepDTO {
    private Long id;
    private Long stepNumber;
    private String text;
}