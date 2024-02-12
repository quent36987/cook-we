package com.cookwe;

import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.CommentRepository;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.data.repository.RecipeStepRepository;
import com.cookwe.data.repository.UserRepository;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.CreateRecipeRequest;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class RecipeEndpointTest {

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

    @MockBean
    private UserService userService;

    private static final String USERNAME_1 = "test-username";
    private static final String PASSWORD_1 = "test-password";
    private static final String EMAIL_1 = "test@test.fr";
    private static final String USERNAME_2 = "test-username2";
    private static final String PASSWORD_2 = "test-password2";
    private static final String EMAIL_2 = "test@test.fr2";
    private static final String COOKIE_NAME = "bezkoder-jwt";

    private static String cookie = "";


    @AfterEach
    public void cleanup() {
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
    public void testCreateRecipeWithoutLogin() throws Exception {
        CreateRecipeRequest recipe = new CreateRecipeRequest()
                .withName("test")
                .withTime(10L)
                .withSeason("spring")
                .withSteps(Arrays.asList("step1", "step2"));


        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testCreateRecipeWithLogin() throws Exception {
        CreateRecipeRequest recipe = new CreateRecipeRequest()
                .withName("test")
                .withTime(10L)
                .withSeason("spring")
                .withSteps(Arrays.asList("step1", "step2"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.time").value(10))
                .andExpect(jsonPath("$.season").value("SPRING"));

    }

    @Test
    public void testGetAllRecipes() throws Exception {
        CreateRecipeRequest recipe = new CreateRecipeRequest()
                .withName("test")
                .withTime(10L)
                .withSeason("spring")
                .withSteps(Arrays.asList("step1", "step2"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.time").value(10))
                .andExpect(jsonPath("$.season").value("SPRING"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    public void testGetRecipeWithId() throws Exception {
        CreateRecipeRequest recipe = new CreateRecipeRequest()
                .withName("test")
                .withTime(10L)
                .withSeason("spring")
                .withSteps(Arrays.asList("step1", "step2"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.time").value(10))
                .andExpect(jsonPath("$.season").value("SPRING"))
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        RecipeResponse response = objectMapper.readValue(contentAsString, RecipeResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipe/" + response.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.time").value(10))
                .andExpect(jsonPath("$.season").value("SPRING"))
                .andExpect(jsonPath("$.steps").isArray())
                .andExpect(jsonPath("$.user.username").value(USERNAME_1));

    }
}
