package com.cookwe.domain.service;

import com.cookwe.data.model.ERole;
import com.cookwe.data.model.RoleModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.UserRepositoryCustom;
import com.cookwe.data.repository.interfaces.RoleRepository;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.domain.entity.RoleEntity;
import com.cookwe.domain.entity.UserEntity;
import com.cookwe.utils.converters.RoleModelToRoleEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Data
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Transactional
    public List<RoleEntity> getRolesByUserId(Long userId) {
        UserModel user = userRepositoryCustom.getUserById(userId);

        return RoleModelToRoleEntity.convertList(user.getRoles());
    }

    @Transactional
    public List<RoleEntity> getAllRoles() {
        List<RoleModel> roles = roleRepository.findAll();

        return RoleModelToRoleEntity.convertList(roles);
    }

    @Transactional
    public List<RoleEntity> getRolesByUsername(String username) {
        UserModel userModel = userRepositoryCustom.getUserByUsername(username);

        return RoleModelToRoleEntity.convertList(userModel.getRoles());
    }

    public ERole getRoleByName(String name) {
        try {
            return ERole.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw RestError.ROLE_NOT_FOUND.get(name);
        }
    }

    public RoleModel getRoleModelByName(String name) {
        Optional<RoleModel> roleModel = roleRepository.findByName(getRoleByName(name));

        if (roleModel.isEmpty()) {
            throw RestError.ROLE_NOT_FOUND.get(name);
        }

        return roleModel.get();
    }

    @Transactional
    public void addRoleToUser(String username, String role) {
        UserModel user = userRepositoryCustom.getUserByUsername(username);
        RoleModel roleModel = getRoleModelByName(role);

        List<RoleModel> roles = userRepository.findRolesByUserId(user.getId());

        if (roles.contains(roleModel)) {
            throw RestError.ROLE_ALREADY_EXISTS.get(role);
        }

        roles.add(roleModel);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(String username, String role) {
        UserModel user = userRepositoryCustom.getUserByUsername(username);
        RoleModel roleModel = getRoleModelByName(role);

        List<RoleModel> roles = userRepository.findRolesByUserId(user.getId());

        if (!roles.contains(roleModel)) {
            throw RestError.ROLE_NOT_PRESENT.get(role);
        }

        roles.remove(roleModel);
        user.setRoles(roles);

        userRepository.save(user);
    }


}
