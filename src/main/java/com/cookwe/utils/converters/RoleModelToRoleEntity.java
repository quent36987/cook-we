package com.cookwe.utils.converters;

import com.cookwe.data.model.ERole;
import com.cookwe.data.model.RoleModel;
import com.cookwe.domain.entity.RoleEntity;

import java.util.ArrayList;
import java.util.List;

public class RoleModelToRoleEntity {
    public static RoleEntity convert(RoleModel roleModel) {
        return new RoleEntity().withId(roleModel.getId())
                .withRole(roleModel.getName().toString());
    }

    public static List<RoleEntity> convertList(Iterable<RoleModel> roleModels) {
        List<RoleEntity> responses = new ArrayList<>();
        for (RoleModel roleModel : roleModels) {
            responses.add(convert(roleModel));
        }
        return responses;
    }
}
