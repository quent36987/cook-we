package com.cookwe.data.repository;

import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.utils.errors.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryCustom {
    @Autowired
    private UserRepository userRepository;

    public UserModel getUserById(Long id) {
        Optional<UserModel> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return user.get();
    }

    public UserModel getUserByUsername(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return user.get();
    }
}
