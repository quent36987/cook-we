package com.cookwe.utils.converters;

import com.cookwe.domain.entity.UserEntity;
import com.cookwe.presentation.response.UserDetailResponse;

import java.util.ArrayList;
import java.util.List;

public class UserEntityToUserDetailResponse {
    public static UserDetailResponse convert(UserEntity userEntity) {
        return new UserDetailResponse().withFirstName(userEntity.getFirstName())
                .withLastName(userEntity.getLastName())
                .withUsername(userEntity.getUsername())
                .withEmail(userEntity.getEmail());
    }

    public static List<UserDetailResponse> convertList(Iterable<UserEntity> userEntities) {
        List<UserDetailResponse> responses = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            responses.add(convert(userEntity));
        }
        return responses;
    }
}
