package com.cookwe;

import com.cookwe.data.repository.interfaces.CommentRepository;
import com.cookwe.data.repository.interfaces.RecipeRepository;
import com.cookwe.data.repository.interfaces.RecipeStepRepository;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.RecipeRequest;
import com.cookwe.presentation.request.LoginRequest;
import com.cookwe.presentation.request.SignupRequest;
import com.cookwe.presentation.response.RecipeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

import static com.cookwe.TestUtils.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class RecipeEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeStepRepository recipeStepRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    private static final String USERNAME_1 = "test-username";
    private static final String PASSWORD_1 = "test-password";
    private static final String EMAIL_1 = "test@test.fr";
    private static final String USERNAME_2 = "test-username2";
    private static final String PASSWORD_2 = "test-password2";
    private static final String EMAIL_2 = "test@test.fr2";
    private static final String COOKIE_NAME = "bezkoder-jwt";

    private static String cookie = "";


    public void createuser() {
        userService.createUser(USERNAME_1, EMAIL_1, PASSWORD_1);
    }

    @BeforeEach
    public void SetUpSecurity() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME_1);
        signupRequest.setEmail(EMAIL_1);
        signupRequest.setPassword(PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // createuser();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(USERNAME_1);
        loginRequest.setPassword(PASSWORD_1);

        cookie = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_NAME)
                .getValue();
    }

    @Test
    void testCreateRecipeWithoutLogin() throws Exception {
        RecipeRequest recipe = createSimpleRecipeRequest();


        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testCreateRecipeWithLogin() throws Exception {
        RecipeRequest recipe = createSimpleRecipeRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value(recipe.name))
                .andExpect(jsonPath("$.time").value(recipe.time))
                .andExpect(jsonPath("$.season").value(recipe.season));

    }

    @Test
    void testGetAllRecipes() throws Exception {
        RecipeRequest recipe = createSimpleRecipeRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value(recipe.name))
                .andExpect(jsonPath("$.time").value(recipe.time))
                .andExpect(jsonPath("$.type").value(recipe.type))
                .andExpect(jsonPath("$.season").value(recipe.season));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    void testGetRecipeWithId() throws Exception {
        RecipeRequest recipe = createSimpleRecipeRequest();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value(recipe.name))
                .andExpect(jsonPath("$.time").value(recipe.time))
                .andExpect(jsonPath("$.season").value(recipe.season))
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        RecipeResponse response = objectMapper.readValue(contentAsString, RecipeResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/" + response.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value(recipe.name))
                .andExpect(jsonPath("$.time").value(recipe.time))
                .andExpect(jsonPath("$.season").value(recipe.season))
                .andExpect(jsonPath("$.steps").isArray());
        // .andExpect(jsonPath("$.user.username").value(USERNAME_1));
    }

    @Test
    void testGetRecipeWithIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/2242322333")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateAndDeleteRecipe() throws Exception {
        RecipeRequest recipeRequest = TestUtils.createRecipeRequestWithIngredients();
        recipeRequest.setSteps(TestUtils.getRecipeSteps());

        RecipeResponse recipeResponse = TestUtils.createRecipe(mockMvc, objectMapper, cookie, recipeRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(recipeRequest.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").value(recipeRequest.time))
                .andExpect(MockMvcResultMatchers.jsonPath("$.season").value(recipeRequest.season))
                .andExpect(MockMvcResultMatchers.jsonPath("$.steps").isArray());

        //get ingredient
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_INGREDIENTS));

        //get steps
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/" + recipeResponse.id + "/steps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_STEPS));

        RecipeRequest updatedRecipe = TestUtils.createRecipeRequestWithIngredientsBis();
        updatedRecipe.setSteps(TestUtils.getRecipeStepsBis());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRecipe))
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updatedRecipe.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").value(updatedRecipe.time))
                .andExpect(MockMvcResultMatchers.jsonPath("$.season").value(updatedRecipe.season))
                .andExpect(MockMvcResultMatchers.jsonPath("$.steps").isArray());

        //get ingredient
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_INGREDIENTS_BIS));

        //get steps
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/" + recipeResponse.id + "/steps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_STEPS_BIS));

        //delete recipe
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void testSearchINgredientinRecipe() throws Exception {
        RecipeRequest recipeRequest = TestUtils.createRecipeRequestWithIngredients();
        recipeRequest.setSteps(TestUtils.getRecipeSteps());

        RecipeResponse recipeResponse = TestUtils.createRecipe(mockMvc, objectMapper, cookie, recipeRequest);

        //empty search
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/ingredients/search?ingredients=dsf,sdffds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

        //search
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/ingredients/search?ingredients=test-ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(recipeResponse.id));
    }
}
