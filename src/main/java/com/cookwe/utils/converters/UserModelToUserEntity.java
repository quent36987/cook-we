package com.cookwe.utils.converters;

import com.cookwe.data.model.UserModel;
import com.cookwe.domain.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserModelToUserEntity {
    public static UserEntity convert(UserModel userModel) {
        return new UserEntity().withId(userModel.getId())
                .withUsername(userModel.getUsername())
                .withEmail(userModel.getEmail());
    }

    public static List<UserEntity> convertList(List<UserModel> userModels) {
        List<UserEntity> responses = new ArrayList<>();
        for (UserModel userModel : userModels) {
            responses.add(convert(userModel));
        }
        return responses;
    }

    public static List<UserEntity> convertSet(Set<UserModel> userModels) {
        List<UserEntity> responses = new ArrayList<>();
        for (UserModel userModel : userModels) {
            responses.add(convert(userModel));
        }
        return responses;
    }


    
}
