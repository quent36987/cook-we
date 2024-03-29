package com.cookwe.domain.mapper;

import com.cookwe.data.model.CommentModel;
import com.cookwe.domain.entity.CommentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "ownerUsername", source = "user.username")
    @Mapping(target = "recipeId", source = "recipe.id")
    @Mapping(target = "createdAt", source = "createdAt")
    CommentDTO toDTO(CommentModel comment);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "recipe.id", source = "recipeId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "user", ignore = true)
    CommentModel toModel(CommentDTO comment);

    List<CommentDTO> toDTOList(List<CommentModel> comments);

    List<CommentModel> toModelList(List<CommentDTO> comments);
}
