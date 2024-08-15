package com.cookwe.presentation.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class ShoppingListRecipeRequest {
    public Long recipeId;
    public int portion;
    List<String> ingredients;
}
