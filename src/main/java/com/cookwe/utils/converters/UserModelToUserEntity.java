package com.cookwe.utils.converters;

import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserModelToUserEntity {
    public static UserEntity convert(UserModel userModel) {
        return new UserEntity().withId(userModel.getId())
                .withUsername(userModel.getUsername())
                .withFirstName(userModel.getFirstName())
                .withLastName(userModel.getLastName())
                .withEmail(userModel.getEmail())
                .withRoles(RoleModelToRoleEntity.convertList(userModel.getRoles()))
                .withEmail(userModel.getEmail());
    }

    public static List<UserEntity> convertList(Iterable<UserModel> userModels) {
        List<UserEntity> responses = new ArrayList<>();
        for (UserModel userModel : userModels) {
            responses.add(convert(userModel));
        }
        return responses;
    }
}
