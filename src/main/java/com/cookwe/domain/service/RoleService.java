package com.cookwe.domain.service;

import com.cookwe.data.model.ERole;
import com.cookwe.data.model.RoleModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.UserRepositoryCustom;
import com.cookwe.data.repository.interfaces.RoleRepository;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.domain.entity.RoleDTO;
import com.cookwe.domain.mapper.RoleMapper;
import com.cookwe.utils.errors.RestError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final RoleMapper roleMapper;

    RoleService(RoleRepository roleRepository, UserRepository userRepository, UserRepositoryCustom userRepositoryCustom, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRepositoryCustom = userRepositoryCustom;
        this.roleMapper = roleMapper;
    }


    public List<RoleDTO> getRolesByUserId(Long userId) {
        UserModel user = userRepositoryCustom.getUserById(userId);

        return roleMapper.toDTOList(user.getRoles());
    }

    public List<RoleDTO> getAllRoles() {
        List<RoleModel> roles = roleRepository.findAll();

        return roleMapper.toDTOList(roles);
    }

    public List<RoleDTO> getRolesByUsername(String username) {
        UserModel userModel = userRepositoryCustom.getUserByUsername(username);

        return roleMapper.toDTOList(userModel.getRoles());
    }

    private ERole getRoleByName(String name) {
        try {
            return ERole.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw RestError.ROLE_NOT_FOUND.get(name);
        }
    }

    private RoleModel getRoleModelByName(String name) {
        Optional<RoleModel> roleModel = roleRepository.findByName(getRoleByName(name));

        if (roleModel.isEmpty()) {
            throw RestError.ROLE_NOT_FOUND.get(name);
        }

        return roleModel.get();
    }

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
