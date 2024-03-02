package com.cookwe;

import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.RecipeRequest;
import com.cookwe.presentation.request.UpdateUserRequest;
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
class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static String cookie = "";

    @Test
    void testGetWitoutAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/favorites-recipes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testAddAndDeleteFavoriteRecipe() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        RecipeRequest recipeRequest = TestUtils.createSimpleRecipeRequest();
        RecipeDTO recipeResponse = TestUtils.createRecipe(mockMvc, objectMapper, cookie, recipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/favorites-recipes/" + recipeResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/favorites-recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(recipeResponse.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(recipeResponse.getName()));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/favorites-recipes/" + recipeResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/favorites-recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void testAddAndDeleteTwoTime() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        RecipeRequest recipeRequest = TestUtils.createSimpleRecipeRequest();
        RecipeDTO recipeResponse = TestUtils.createRecipe(mockMvc, objectMapper, cookie, recipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/favorites-recipes/" + recipeResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/favorites-recipes/" + recipeResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/favorites-recipes/" + recipeResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/favorites-recipes/" + recipeResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testGetUserByUsername() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        TestUtils.createuser(userService, TestUtils.USERNAME_2, TestUtils.EMAIL_2, TestUtils.PASSWORD_2);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(TestUtils.USERNAME_1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + TestUtils.USERNAME_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(TestUtils.USERNAME_2));
    }

    @Test
    void testGetUserRecipes() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());

        RecipeRequest recipeRequest = TestUtils.createSimpleRecipeRequest();
        TestUtils.createRecipe(mockMvc, objectMapper, cookie, recipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(recipeRequest.getName()));
    }

    @Test
    void testGetUserRecipeByUsername() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        TestUtils.createuser(userService, TestUtils.USERNAME_2, TestUtils.EMAIL_2, TestUtils.PASSWORD_2);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        RecipeRequest recipeRequest = TestUtils.createSimpleRecipeRequest();
        TestUtils.createRecipe(mockMvc, objectMapper, cookie, recipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + TestUtils.USERNAME_1 + "/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(recipeRequest.getName()));

        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_2, TestUtils.PASSWORD_2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + TestUtils.USERNAME_1 + "/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(recipeRequest.getName()));
    }

    @Test
    void testUpdateDetailUser() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(TestUtils.USERNAME_1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(TestUtils.EMAIL_1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

        UpdateUserRequest updateUserRequest = new UpdateUserRequest().withFirstName("test").withLastName("test2");
        String result = "{\"username\":\"" + TestUtils.USERNAME_1 + "\",\"email\":\"" + TestUtils.EMAIL_1 + "\",\"firstName\":\"test\",\"lastName\":\"test2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(result));

    }


}
