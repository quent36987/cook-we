package com.cookwe;

import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.CreateIngredientRequest;
import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.request.LoginRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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

import java.util.ArrayList;
import java.util.Objects;

public class UtilsTest {
    public static final String USERNAME_1 = "username";
    public static final String PASSWORD_1 = "test-password";
    public static final String EMAIL_1 = "test@test.fr";
    public static final String USERNAME_2 = "username2";
    public static final String PASSWORD_2 = "test-password2";
    public static final String EMAIL_2 = "test@test.fr2";
    public static final String COOKIE_NAME = "bezkoder-jwt";


    public static void createuser(UserService userService, String username, String email, String password) throws Exception {
        userService.createUser(username, email, password);
    }


    public static String setUpSecurity(MockMvc mockMvc, ObjectMapper objectMapper, String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        return Objects.requireNonNull(mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .getResponse()
                        .getCookie(COOKIE_NAME))
                .getValue();
    }


    public static CreateRecipeRequest createSimpleRecipeRequest() {
        CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest();
        createRecipeRequest.setName("test-recipe");
        createRecipeRequest.setTime(10L);
        createRecipeRequest.setPortions(4L);
        createRecipeRequest.setSeason("SUMMER");
        createRecipeRequest.setIngredients(new ArrayList<>());
        createRecipeRequest.setSteps(new ArrayList<>());

        return createRecipeRequest;
    }


    public static String STRING_INGREDIENTS = "[{\"name\":\"test-ingredient\",\"quantity\":10.0,\"unit\":\"MILLILITER\"},{\"name\":\"test-ingredient2\",\"quantity\":20.0,\"unit\":\"GRAM\"}]";

    public static CreateRecipeRequest createRecipeRequestWithIngredients() {
        CreateRecipeRequest createRecipeRequest = createSimpleRecipeRequest();
        createRecipeRequest.getIngredients().add(new CreateIngredientRequest("test-ingredient", 10F, "MILLILITER"));
        createRecipeRequest.getIngredients().add(new CreateIngredientRequest("test-ingredient2", 20F, "GRAM"));

        return createRecipeRequest;
    }

    public static RecipeResponse createRecipe(MockMvc mockMvc, ObjectMapper objectMapper, String cookie, CreateRecipeRequest recipe) throws Exception {
        return objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), RecipeResponse.class);
    }
}
