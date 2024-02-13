package com.cookwe;

import com.cookwe.data.repository.CommentRepository;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.data.repository.RecipeStepRepository;
import com.cookwe.data.repository.UserRepository;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.CreateCommentRequest;
import com.cookwe.presentation.request.CreateRecipeRequest;
import com.cookwe.presentation.request.LoginRequest;
import com.cookwe.presentation.request.SignupRequest;
import com.cookwe.presentation.response.CommentResponse;
import com.cookwe.presentation.response.RecipeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CommentEndpointTest {

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


    public void SetUpSecurity(String username, String password) throws Exception {
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

    @Test
    public void testCreateComment() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        SetUpSecurity(USERNAME_1, PASSWORD_1);

        CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest();
        createRecipeRequest.setName("test-recipe");
        createRecipeRequest.setTime(10L);
        createRecipeRequest.setPortions(2L);
        createRecipeRequest.setSeason("spring");
        createRecipeRequest.setSteps(Arrays.asList("step1", "step2"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        RecipeResponse recipeResponse = objectMapper.readValue(result.getResponse().getContentAsString(), RecipeResponse.class);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("ceci est un commenatire");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // try to get the comment
        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].text").value("ceci est un commenatire"))
                .andReturn();

        List<CommentResponse> commentResponse = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), CommentResponse[].class));

        // try to delete the comment
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/" + commentResponse.get(0).id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // try to get the comment
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testCreateCommentWithWrongRecipeId() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        SetUpSecurity(USERNAME_1, PASSWORD_1);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("ceci est un commenatire");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/recipe/123324232")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreateAndDeleteDeniedComment() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        SetUpSecurity(USERNAME_1, PASSWORD_1);

        CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest();
        createRecipeRequest.setName("test-recipe");
        createRecipeRequest.setTime(10L);
        createRecipeRequest.setPortions(2L);
        createRecipeRequest.setSeason("spring");
        createRecipeRequest.setSteps(Arrays.asList("step1", "step2"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        RecipeResponse recipeResponse = objectMapper.readValue(result.getResponse().getContentAsString(), RecipeResponse.class);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("ceci est un commenatire");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // try to get the comment
        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].text").value("ceci est un commenatire"))
                .andReturn();

        List<CommentResponse> commentResponse = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), CommentResponse[].class));

        // try to delete the comment with another user
        createuser(USERNAME_2, EMAIL_2, PASSWORD_2);
        SetUpSecurity(USERNAME_2, PASSWORD_2);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/" + commentResponse.get(0).id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // try to delete the comment with the owner
        SetUpSecurity(USERNAME_1, PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/" + commentResponse.get(0).id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // try to get the comment
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testCreateAndUpdateComment() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        SetUpSecurity(USERNAME_1, PASSWORD_1);

        CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest();
        createRecipeRequest.setName("test-recipe");
        createRecipeRequest.setTime(10L);
        createRecipeRequest.setPortions(2L);
        createRecipeRequest.setSeason("spring");
        createRecipeRequest.setSteps(Arrays.asList("step1", "step2"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        RecipeResponse recipeResponse = objectMapper.readValue(result.getResponse().getContentAsString(), RecipeResponse.class);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("ceci est un commenatire");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // try to get the comment
        result = mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].text").value("ceci est un commenatire"))
                .andReturn();

        List<CommentResponse> commentResponse = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), CommentResponse[].class));

        createCommentRequest = new CreateCommentRequest("ceci est un commenatire modifié");

        // try to update the comment
        mockMvc.perform(MockMvcRequestBuilders.put("/api/comments/" + commentResponse.get(0).id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());


        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].text").value("ceci est un commenatire modifié"));

    }

    @Test
    public void testCommentWithoutText() throws Exception {
        createuser(USERNAME_1, EMAIL_1, PASSWORD_1);
        SetUpSecurity(USERNAME_1, PASSWORD_1);

        CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest();
        createRecipeRequest.setName("test-recipe");
        createRecipeRequest.setTime(10L);
        createRecipeRequest.setPortions(2L);
        createRecipeRequest.setSeason("spring");
        createRecipeRequest.setSteps(Arrays.asList("step1", "step2"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRecipeRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        RecipeResponse recipeResponse = objectMapper.readValue(result.getResponse().getContentAsString(), RecipeResponse.class);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/recipe/" + recipeResponse.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest))
                        .cookie(new Cookie(COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
