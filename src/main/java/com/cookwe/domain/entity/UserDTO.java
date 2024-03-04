package com.cookwe.domain.entity;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private List<RoleDTO> roles;
    private List<Long> favoriteRecipeIds;
}

