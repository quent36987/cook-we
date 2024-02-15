package com.cookwe.domain.entity;

import com.cookwe.data.model.EUnit;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class IngredientEntity {
    private Long id;
    private String name;
    private Float quantity;
    private EUnit unit;
}
