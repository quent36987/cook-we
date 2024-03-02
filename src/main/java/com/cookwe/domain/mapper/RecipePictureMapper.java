package com.cookwe.domain.mapper;

import com.cookwe.data.model.RecipePictureModel;
import com.cookwe.domain.entity.RecipePictureDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipePictureMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "ownerUsername", source = "user.username")
    @Mapping(target = "createdAt", source = "createdAt")
    RecipePictureDTO toDTO(RecipePictureModel picture);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "createdAt", source = "createdAt")
    RecipePictureModel toModel(RecipePictureDTO picture);

    List<RecipePictureDTO> toDTOList(List<RecipePictureModel> pictures);

    List<RecipePictureModel> toModelList(List<RecipePictureDTO> pictures);
}
