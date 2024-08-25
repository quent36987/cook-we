package com.cookwe.domain.service;

import com.cookwe.data.model.*;
import com.cookwe.data.repository.RecipeRepositoryCustom;
import com.cookwe.data.repository.UserRepositoryCustom;
import com.cookwe.data.repository.interfaces.RecipeRepository;
import com.cookwe.data.repository.interfaces.RoleRepository;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.entity.UserDTO;
import com.cookwe.domain.mapper.RecipeMapper;
import com.cookwe.domain.mapper.UserMapper;
import com.cookwe.utils.errors.RestError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final RecipeRepositoryCustom recipeRepositoryCustom;
    private final RecipeRepository recipeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final RecipeMapper recipeMapper;

    public UserService(RecipeMapper recipeMapper, UserMapper userMapper, UserRepository userRepository, UserRepositoryCustom userRepositoryCustom, RecipeRepositoryCustom recipeRepositoryCustom, RecipeRepository recipeRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userRepositoryCustom = userRepositoryCustom;
        this.recipeRepositoryCustom = recipeRepositoryCustom;
        this.recipeRepository = recipeRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.userMapper = userMapper;
        this.recipeMapper = recipeMapper;
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userMapper.toDTO(userRepositoryCustom.getUserById(id));
    }


    @Transactional(readOnly = true)
    public List<RecipeDTO> getFavoriteRecipes(Long userId) {
        List<RecipeModel> favoriteRecipes = userRepository.findFavoriteRecipesByUserId(userId);

        return recipeMapper.toDTOList(favoriteRecipes);
    }


    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw RestError.USER_NOT_FOUND.get();
        }

        return userMapper.toDTO(user.get());
    }

    private Iterable<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByUsername(String username) {
        UserDTO user = getUserByUsername(username);

        List<RecipeModel> recipes = recipeRepository.findByUserId(user.getId());

        return recipeMapper.toDTOList(recipes);
    }

    @Transactional
    public String resetPassword(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(RestError.USER_NOT_FOUND::get);

        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(encoder.encode(newPassword));

        userRepository.save(user);

        return newPassword;
    }

    @Transactional
    public void changePassword(String username, String password) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(RestError.USER_NOT_FOUND::get);

        user.setPassword(encoder.encode(password));

        userRepository.save(user);
    }

    public void createUser(String username, String email, String password) {
        UserModel user = new UserModel(username, email, encoder.encode(password));
        List<RoleModel> roles = new ArrayList<>();

        RoleModel userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role non trouvé."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
    }

    public UserDTO updateUserDetails(Long id, String firstName, String lastName) {
        UserModel user = userRepositoryCustom.getUserById(id);

        user.setFirstName(firstName);
        user.setLastName(lastName);

        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    public void addFavoriteRecipe(Long userId, Long recipeId) {
        UserModel user = userRepositoryCustom.getUserById(userId);
        RecipeModel favoriteRecipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        List<RecipeModel> favoriteRecipes = userRepository.findFavoriteRecipesByUserId(userId);

        if (favoriteRecipes.contains(favoriteRecipe)) {
            throw RestError.RECIPE_ALREADY_FAVORITE.get(favoriteRecipe.getName());
        }

        favoriteRecipes.add(favoriteRecipe);
        user.setFavoriteRecipes(favoriteRecipes);
    }

    public void removeFavoriteRecipe(Long userId, Long recipeId) {
        UserModel user = userRepositoryCustom.getUserById(userId);
        RecipeModel favoriteRecipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        List<RecipeModel> favoriteRecipes = userRepository.findFavoriteRecipesByUserId(userId);

        if (!favoriteRecipes.contains(favoriteRecipe)) {
            throw RestError.RECIPE_NOT_FAVORITE.get(favoriteRecipe.getName());
        }

        favoriteRecipes.remove(favoriteRecipe);
        user.setFavoriteRecipes(favoriteRecipes);
    }
}
