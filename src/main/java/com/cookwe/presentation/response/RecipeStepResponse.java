package com.cookwe.presentation.response;

import lombok.*;

@With
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepResponse {
    private Long stepNumber;
    private String text;
}
