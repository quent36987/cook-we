package com.cookwe.domain.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListDetailDTO {
    private Long id;
    private String name;
    private String ownerUsername;
    private String createdAt;
    private List<RecipeShoppingListDTO> recipes;
    private List<IngredientShoppingListDTO> ingredients;
    private List<String> sharedUsers;
}
