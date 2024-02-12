package com.cookwe.utils.converters;

import com.cookwe.domain.entity.UserEntity;
import com.cookwe.presentation.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class UserEntityToUserResponse {
    public static UserResponse convert(UserEntity userEntity) {
        return new UserResponse()
                .withUsername(userEntity.getUsername());
    }

    public static List<UserResponse> convertList(List<UserEntity> userEntities) {
        List<UserResponse> responses = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            responses.add(convert(userEntity));
        }
        return responses;
    }
}
