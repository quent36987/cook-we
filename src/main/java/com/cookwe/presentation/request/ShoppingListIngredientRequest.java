package com.cookwe.presentation.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class ShoppingListIngredientRequest {
    public String name;
    public Long id;
}
