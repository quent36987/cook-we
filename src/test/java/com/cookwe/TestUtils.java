package com.cookwe;

import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.IngredientRequest;
import com.cookwe.presentation.request.RecipeRequest;
import com.cookwe.presentation.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class TestUtils {
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


    public static MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
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


    public static RecipeRequest createSimpleRecipeRequest() {
        RecipeRequest recipeRequest = new RecipeRequest();
        recipeRequest.setName("test-recipe");
        recipeRequest.setTime(10L);
        recipeRequest.setPortions(4L);
        recipeRequest.setSeason("SUMMER");
        recipeRequest.setType("DESSERT");
        recipeRequest.setIngredients(new ArrayList<>());
        recipeRequest.setSteps(new ArrayList<>());

        return recipeRequest;
    }

    public static RecipeRequest createSimpleRecipeRequestBis() {
        RecipeRequest recipeRequest = new RecipeRequest();
        recipeRequest.setName("test-bis");
        recipeRequest.setTime(1L);
        recipeRequest.setPortions(40L);
        recipeRequest.setSeason("WINTER");
        recipeRequest.setType("ENTREE");
        recipeRequest.setIngredients(new ArrayList<>());
        recipeRequest.setSteps(new ArrayList<>());

        return recipeRequest;
    }


    public static String STRING_INGREDIENTS = "[{\"name\":\"test-ingredient\",\"quantity\":10.0,\"unit\":\"MILLILITER\"},{\"name\":\"test-ingredient2\",\"quantity\":20.0,\"unit\":\"GRAM\"}]";

    public static RecipeRequest createRecipeRequestWithIngredients() {
        RecipeRequest recipeRequest = createSimpleRecipeRequest();
        recipeRequest.getIngredients().add(new IngredientRequest("test-ingredient", 10F, "MILLILITER"));
        recipeRequest.getIngredients().add(new IngredientRequest("test-ingredient2", 20F, "GRAM"));

        return recipeRequest;
    }

    public static String STRING_INGREDIENTS_BIS = "[{\"name\":\"test-bis\",\"quantity\":1.5,\"unit\":\"PIECE\"},{\"name\":\"test-bis2\",\"quantity\":2.0,\"unit\":\"CUP\"}]";

    public static RecipeRequest createRecipeRequestWithIngredientsBis() {
        RecipeRequest recipeRequest = createSimpleRecipeRequestBis();
        recipeRequest.getIngredients().add(new IngredientRequest("test-bis", 1.5F, "PIECE"));
        recipeRequest.getIngredients().add(new IngredientRequest("test-bis2", 2F, "CUP"));

        return recipeRequest;
    }

    public static String STRING_STEPS = "[{\"stepNumber\":0,\"text\":\"step 1\"},{\"stepNumber\":1,\"text\":\"step 2\"}]";

    public static List<String> getRecipeSteps() {
        List<String> steps = new ArrayList<>();
        steps.add("step 1");
        steps.add("step 2");

        return steps;
    }

    public static String STRING_STEPS_BIS = "[{\"stepNumber\":0,\"text\":\"step 1 bis\"},{\"stepNumber\":1,\"text\":\"step 2 bis\"}]";

    public static List<String> getRecipeStepsBis() {
        List<String> steps = new ArrayList<>();
        steps.add("step 1 bis");
        steps.add("step 2 bis");

        return steps;
    }


    public static RecipeDTO createRecipe(MockMvc mockMvc, ObjectMapper objectMapper, String cookie, RecipeRequest recipe) throws Exception {
        return objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), RecipeDTO.class);
    }
}
