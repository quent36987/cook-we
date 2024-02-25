package com.cookwe.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class RecipeRequest {
    public String name;

    public Long time;

    public Long portions;

    @Schema(type = "string", allowableValues = {"SPRING", "SUMMER", "AUTUMN", "WINTER", "ALL"})
    public String season;

    @Schema(type = "string", allowableValues = {"ENTREE", "PLAT", "DESSERT"})
    public String type;

    public List<String> steps;

    public List<IngredientRequest> ingredients;
}
