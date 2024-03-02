package com.cookwe.domain.mapper;

import com.cookwe.data.model.RoleModel;
import com.cookwe.domain.entity.RoleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleDTO toDTO(RoleModel role);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleModel toModel(RoleDTO role);

    List<RoleDTO> toDTOList(List<RoleModel> roles);

    List<RoleModel> toModelList(List<RoleDTO> roles);
}
