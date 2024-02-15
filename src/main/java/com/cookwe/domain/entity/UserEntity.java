package com.cookwe.domain.entity;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@With
@NoArgsConstructor
public class UserEntity {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private List<RoleEntity> roles;
    private List<RecipeEntity> favoriteRecipes = new ArrayList<>();
}

