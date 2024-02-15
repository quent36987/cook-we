package com.cookwe.presentation.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class CreateRecipeRequest {
    public String name;

    public Long time;

    public Long portions;

    public String season;

    public List<String> steps;

    public List<CreateIngredientRequest> ingredients;
}
