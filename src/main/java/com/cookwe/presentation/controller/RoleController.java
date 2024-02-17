package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.RoleEntity;
import com.cookwe.domain.service.RoleService;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.presentation.response.RoleResponse;
import com.cookwe.utils.converters.RoleEntityToRoleResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role", description = "Role operations")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class RoleController {

    @Autowired
    RoleService roleService;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all roles")
    public List<RoleResponse> getAllRoles() {
        List<RoleEntity> roles = roleService.getAllRoles();

        return RoleEntityToRoleResponse.convertList(roles);
    }

    @GetMapping("/users")
    @Operation(summary = "Get my roles")
    public List<RoleResponse> getAllRolesByUserId() {
        List<RoleEntity> roles = roleService.getRolesByUserId(getUserId());

        return RoleEntityToRoleResponse.convertList(roles);
    }

    @GetMapping("/users/{username}")
    @Operation(summary = "Get roles by username (need ADMIN role)")
    @Parameter(name = "username", description = "The username of the user")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getRolesByUsername(@PathVariable String username) {
        List<RoleEntity> roles = roleService.getRolesByUsername(username);

        return RoleEntityToRoleResponse.convertList(roles);
    }

    @PostMapping("{roleName}/users/{username}")
    @Operation(summary = "Add role to user (need ADMIN role)")
    @Parameter(name = "roleName", description = "The name of the role")
    @Parameter(name = "username", description = "The username of the user")
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse addRoleToUser(@PathVariable String roleName, @PathVariable String username) {
        roleService.addRoleToUser(username, roleName);

        return new MessageResponse("Role added to user");
    }

    @DeleteMapping("{roleName}/users/{username}")
    @Operation(summary = "Remove role from user (need ADMIN role)")
    @Parameter(name = "roleName", description = "The name of the role")
    @Parameter(name = "username", description = "The username of the user")
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse removeRoleFromUser(@PathVariable String roleName, @PathVariable String username) {
        roleService.removeRoleFromUser(username, roleName);

        return new MessageResponse("Role removed from user");
    }

}
