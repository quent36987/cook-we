package com.cookwe.domain.mapper;

import com.cookwe.data.model.ShoppingListModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.ShoppingListDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RecipeShoppingListMapper.class, IngredientShoppingListMapper.class, UserMapper.class})
public interface ShoppingListDetailMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "ownerUsername", source = "owner.username")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "recipes", source = "recipes")
    @Mapping(target = "ingredients", source = "ingredients")
    @Mapping(target = "sharedUsers", source = "sharedWithUsers", qualifiedByName = "toUsernames")
    ShoppingListDetailDTO toDTO(ShoppingListModel shoppingList);

    List<ShoppingListDetailDTO> toDTOList(List<ShoppingListModel> shoppingLists);

    @Named("toUsernames")
    public static List<String> toUsernames(List<UserModel> sharedWithUsers) {
        return sharedWithUsers.stream().map(UserModel::getUsername).toList();
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "recipes", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    @Mapping(target = "sharedWithUsers", ignore = true)
    ShoppingListModel toModel(ShoppingListDetailDTO shoppingList);
}
