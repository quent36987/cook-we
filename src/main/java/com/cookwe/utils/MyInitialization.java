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
        if (!userService.existsByUsername(adminUsername)) {
            userService.createUser(adminUsername, adminEmail, adminPassword);
            roleService.addRoleToUser(adminUsername, "ROLE_ADMIN");
            roleService.addRoleToUser(adminUsername, "ROLE_MODERATOR");

        }
    }
}

