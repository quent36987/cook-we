package com.cookwe.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class IngredientRequest {
    public String name;
    public Float quantity;

    @Schema(type = "string", allowableValues = {"GRAM", "MILLILITER", "TEASPOON", "TABLESPOON", "CUP", "PIECE", "POT", "PINCH"})
    public String unit;
}
