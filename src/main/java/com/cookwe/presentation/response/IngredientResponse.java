package com.cookwe.presentation.response;

import com.cookwe.data.model.EUnit;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class IngredientResponse {
    private String name;

    private Float quantity;

    private EUnit unit;
}