package com.cookwe.domain.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeShoppingListDTO {
    private Long id;
    private Long recipeId;
    private String imageUrl;
    private String name;
    private int portion;
}
