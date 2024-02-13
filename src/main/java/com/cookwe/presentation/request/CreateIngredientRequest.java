package com.cookwe.presentation.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class CreateIngredientRequest {
    public String name;
    public Float quantity;
    public String unit;
}
