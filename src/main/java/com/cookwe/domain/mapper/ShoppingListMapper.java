package com.cookwe.domain.mapper;

import com.cookwe.data.model.ShoppingListModel;
import com.cookwe.domain.entity.ShoppingListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ShoppingListMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "ownerUsername", source = "owner.username")
    @Mapping(target = "createdAt", source = "createdAt")
    ShoppingListDTO toDTO(ShoppingListModel shoppingList);

    List<ShoppingListDTO> toDTOList(List<ShoppingListModel> shoppingLists);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    ShoppingListModel toModel(ShoppingListDTO shoppingList);
}
