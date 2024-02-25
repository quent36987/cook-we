package com.cookwe.presentation.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class IngredientRequest {
    public String name;
    public Float quantity;
    public String unit;
}
