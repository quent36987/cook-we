package com.cookwe;

import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static String cookie = "";

    @Test
    public void testGetWitoutAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/favorites-recipes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testAddAndDeleteFavoriteRecipe() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        CreateRecipeRequest createRecipeRequest = UtilsTest.createSimpleRecipeRequest();
        RecipeResponse recipeResponse = UtilsTest.createRecipe(mockMvc, objectMapper, cookie,createRecipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/favorites-recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/favorites-recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(recipeResponse.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(recipeResponse.name));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/favorites-recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/favorites-recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    public void testAddAndDeleteTwoTime() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        CreateRecipeRequest createRecipeRequest = UtilsTest.createSimpleRecipeRequest();
        RecipeResponse recipeResponse = UtilsTest.createRecipe(mockMvc, objectMapper, cookie,createRecipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/favorites-recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/favorites-recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/favorites-recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/favorites-recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetUserByUsername() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        UtilsTest.createuser(userService, UtilsTest.USERNAME_2, UtilsTest.EMAIL_2, UtilsTest.PASSWORD_2);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(UtilsTest.USERNAME_1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/" + UtilsTest.USERNAME_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(UtilsTest.USERNAME_2));
    }

    @Test
    public void testGetUserRecipes() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());

        CreateRecipeRequest createRecipeRequest = UtilsTest.createSimpleRecipeRequest();
        UtilsTest.createRecipe(mockMvc, objectMapper, cookie,createRecipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(createRecipeRequest.getName()));
    }

    @Test
    public void testGetUserRecipeByUsername() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        UtilsTest.createuser(userService, UtilsTest.USERNAME_2, UtilsTest.EMAIL_2, UtilsTest.PASSWORD_2);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        CreateRecipeRequest createRecipeRequest = UtilsTest.createSimpleRecipeRequest();
        UtilsTest.createRecipe(mockMvc, objectMapper, cookie,createRecipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/" + UtilsTest.USERNAME_1 + "/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(createRecipeRequest.getName()));

        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_2, UtilsTest.PASSWORD_2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/" + UtilsTest.USERNAME_1 + "/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(createRecipeRequest.getName()));
    }


}
