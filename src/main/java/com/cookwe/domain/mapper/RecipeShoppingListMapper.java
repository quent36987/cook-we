package com.cookwe.domain.mapper;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.ShoppingListRecipeModel;
import com.cookwe.domain.entity.RecipeShoppingListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeShoppingListMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "recipe", qualifiedByName = "getFirstImageUrl")
    @Mapping(target = "recipeId", source = "recipe.id")
    @Mapping(target = "name", source = "recipe.name")
    @Mapping(target = "portion", source = "portion")
    RecipeShoppingListDTO toDTO(ShoppingListRecipeModel recipe);

    @Named("getFirstImageUrl")
    public static String getFirstImageUrl(RecipeModel recipe) {
        return recipe.getPictures().isEmpty() ? null : recipe.getPictures().get(0).getImageUrl();
    }

    List<RecipeShoppingListDTO> toDTOList(List<ShoppingListRecipeModel> recipes);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "shoppingList", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    @Mapping(target = "portion", source = "portion")
    ShoppingListRecipeModel toModel(RecipeShoppingListDTO recipe);
}

