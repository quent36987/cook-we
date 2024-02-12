package com.cookwe.domain.entity;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.RoleModel;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@With
@NoArgsConstructor
public class UserEntity {
    public Long id;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public Set<RoleModel> roles = new HashSet<>();
    public Set<RecipeModel> favoriteRecipes = new HashSet<>();
}

