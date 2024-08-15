package com.cookwe.domain.mapper;


import com.cookwe.data.model.ShoppingListIngredientModel;
import com.cookwe.domain.entity.IngredientShoppingListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredientShoppingListMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "shoppingListRecipeId", source = "shoppingListRecipe.id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "checked", source = "checked")
    IngredientShoppingListDTO toDTO(ShoppingListIngredientModel ingredient);

    List<IngredientShoppingListDTO> toDTOList(List<ShoppingListIngredientModel> ingredients);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "shoppingListRecipe.id", source = "shoppingListRecipeId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "checked", source = "checked")
    ShoppingListIngredientModel toModel(IngredientShoppingListDTO ingredient);
}
