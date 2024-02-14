package com.cookwe.utils.converters;

import com.cookwe.domain.entity.RoleEntity;
import com.cookwe.presentation.response.RoleResponse;

import java.util.ArrayList;
import java.util.List;

public class RoleEntityToRoleResponse {
    public static RoleResponse convert(RoleEntity roleEntity) {
        return new RoleResponse().withRole(roleEntity.getRole());
    }

    public static List<RoleResponse> convertList(Iterable<RoleEntity> roleEntities) {
        List<RoleResponse> responses = new ArrayList<>();
        for (RoleEntity roleEntity : roleEntities) {
            responses.add(convert(roleEntity));
        }
        return responses;
    }
}
