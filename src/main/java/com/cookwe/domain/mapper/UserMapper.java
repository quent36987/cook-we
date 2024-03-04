package com.cookwe.domain.mapper;

import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "favoriteRecipeIds", source = "favoriteRecipes", qualifiedByName = "getFavoriteRecipesIds")
    UserDTO toDTO(UserModel user);

    @Named("getFavoriteRecipesIds")
    public static List<Long> getFavoriteRecipesIds(List<RecipeModel> recipes) {
        return recipes.stream()
                .map(RecipeModel::getId)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "favoriteRecipes", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    @Mapping(target = "pictures", ignore = true)
    UserModel toModel(UserDTO user);

    List<UserDTO> toDTOList(List<UserModel> users);

    List<UserModel> toModelList(List<UserDTO> users);
}
