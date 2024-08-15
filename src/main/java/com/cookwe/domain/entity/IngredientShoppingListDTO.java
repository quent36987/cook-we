package com.cookwe.domain.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngredientShoppingListDTO {
    private Long id;
    private Long shoppingListRecipeId;
    private String name;
    private boolean checked;
}
