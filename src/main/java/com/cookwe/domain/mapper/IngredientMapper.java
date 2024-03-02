package com.cookwe.domain.mapper;

import com.cookwe.data.model.IngredientModel;
import com.cookwe.domain.entity.IngredientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface IngredientMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "unit")
    IngredientDTO toDTO(IngredientModel ingredient);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "unit")
    IngredientModel toModel(IngredientDTO ingredient);

    List<IngredientDTO> toDTOList(List<IngredientModel> ingredients);

    List<IngredientModel> toModelList(List<IngredientDTO> ingredients);
}
