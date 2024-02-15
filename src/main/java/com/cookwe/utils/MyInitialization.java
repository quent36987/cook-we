package com.cookwe.utils;

import com.cookwe.domain.entity.RoleEntity;
import com.cookwe.domain.service.RoleService;
import com.cookwe.domain.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyInitialization implements CommandLineRunner {

    @Value("${cook-we.admin.email}")
    private String adminEmail;

    @Value("${cook-we.admin.password}")
    private String adminPassword;

    @Value("${cook-we.admin.username}")
    private String adminUsername;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("ApplicationRunner initialized");
        if (!userService.existsByUsername(adminUsername)) {
            userService.createUser(adminUsername, adminEmail, adminPassword);

            List<String> roles = roleService.getRolesByUserId(userService.getUserByUsername(adminUsername).getId()).stream().map(RoleEntity::getRole).toList();
            if (!roles.contains("ROLE_ADMIN")) {
                roleService.addRoleToUser(adminUsername, "ROLE_ADMIN");
            }
            if (!roles.contains("ROLE_MODERATOR")) {
                roleService.addRoleToUser(adminUsername, "ROLE_MODERATOR");
            }
        }
    }
}

