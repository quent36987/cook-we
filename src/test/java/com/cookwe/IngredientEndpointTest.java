package com.cookwe;

import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.IngredientRequest;
import com.cookwe.presentation.request.RecipeRequest;
import com.cookwe.presentation.request.LoginRequest;
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
class IngredientEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static final String USERNAME_1 = "username";
    private static final String PASSWORD_1 = "test-password";
    private static final String EMAIL_1 = "test@test.fr";
    private static final String USERNAME_2 = "username2";
    private static final String PASSWORD_2 = "test-password2";
    private static final String EMAIL_2 = "test@test.fr2";
    private static final String COOKIE_NAME = "bezkoder-jwt";

    private static String cookie = "";


    public void createuser(String username, String email, String password) throws Exception {
        userService.createUser(username, email, password);
    }


    public void setUpSecurity(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        cookie = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getCookie(COOKIE_NAME)
                .getValue();
    }



    public static String STRING_INGREDIENTS = "[{\"name\":\"test-ingredient\",\"quantity\":10.0,\"unit\":\"MILLILITER\"},{\"name\":\"test-ingredient2\",\"quantity\":20.0,\"unit\":\"GRAM\"}]";

    public RecipeRequest createRecipeRequestWithIngredients() {
        RecipeRequest recipeRequest = TestUtils.createSimpleRecipeRequest();
        recipeRequest.getIngredients().add(new IngredientRequest("test-ingredient", 10F, "MILLILITER"));
        recipeRequest.getIngredients().add(new IngredientRequest("test-ingredient2", 20F, "GRAM"));

        return recipeRequest;
    }

    public RecipeResponse createRecipe(RecipeRequest recipeRequest) throws Exception {
        return objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), RecipeResponse.class);
    }

    @Test
    void testAddIngredient() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        setUpSecurity(USERNAME_1, PASSWORD_1);

        RecipeResponse recipe = createRecipe(TestUtils.createSimpleRecipeRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient", 10F, "MILLILITER")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{\"name\":\"test-ingredient\",\"quantity\":10.0,\"unit\":\"MILLILITER\"}]"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/ingredients/recipes/" + recipe.id + "/test-ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void testCreateRecipeWithIngredient() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        setUpSecurity(USERNAME_1, PASSWORD_1);

        RecipeResponse recipe = createRecipe(createRecipeRequestWithIngredients());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_INGREDIENTS));
    }

    @Test
    void testAddAndDeleteIngredientAccesDenied() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        setUpSecurity(USERNAME_1, PASSWORD_1);

        RecipeResponse recipe = createRecipe(createRecipeRequestWithIngredients());

        createuser(USERNAME_2, EMAIL_2, PASSWORD_2);
        setUpSecurity(USERNAME_2, PASSWORD_2);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient", 10F, "MILLILITER")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_INGREDIENTS));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/ingredients/recipes/" + recipe.id + "/test-ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_INGREDIENTS));
    }

    @Test
    void testAddBadIngredientValue() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        setUpSecurity(USERNAME_1, PASSWORD_1);

        RecipeResponse recipe = createRecipe(TestUtils.createSimpleRecipeRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient", 0F, "MILLILITER")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient", 10F, "milliliter")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient", -0.1F, "MILLILITER")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("", 1F, "MILLILITER")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10.0, \"unit\":\"MILLILITER\"}")
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test-ingredient\", \"unit\":\"MILLILITER\"}")
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test-ingredient\", \"quantity\":10.0}")
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/23232323")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient", 1F, "MILLILITER")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateIngredient() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        setUpSecurity(USERNAME_1, PASSWORD_1);

        RecipeResponse recipe = createRecipe(createRecipeRequestWithIngredients());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_INGREDIENTS));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient", 5.0F, "CUP")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IngredientRequest("test-ingredient2", 2F, "PIECE")))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredients/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(STRING_INGREDIENTS.replace("10.0", "5.0").replace("20.0", "2.0").replace("MILLILITER", "CUP").replace("GRAM", "PIECE")));
    }


}
