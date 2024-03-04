package com.cookwe.domain.entity;

import com.cookwe.data.model.EUnit;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDTO {
    private Long id;
    private String name;
    private Float quantity;
    private EUnit unit;
}
