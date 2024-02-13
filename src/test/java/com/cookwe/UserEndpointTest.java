package com.cookwe;

import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.data.repository.UserRepository;
import com.cookwe.presentation.request.LoginRequest;
import com.cookwe.presentation.request.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    private static final String USERNAME_1 = "username";
    private static final String PASSWORD_1 = "test-password";
    private static final String EMAIL_1 = "test@test.fr";
    private static final String USERNAME_2 = "username2";
    private static final String PASSWORD_2 = "test-password2";
    private static final String EMAIL_2 = "test@test.fr2";

    @Test
    public void testRegistration() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME_1);
        signupRequest.setEmail(EMAIL_1);
        signupRequest.setPassword(PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAuthentication() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("quentin2");
        loginRequest.setPassword("quentin2");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFailAuthentication() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("nonexistent");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testFailRegistration() throws Exception{
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(USERNAME_1);
        signupRequest.setEmail(EMAIL_1);
        signupRequest.setPassword(PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());


        signupRequest.setUsername(USERNAME_2);
        signupRequest.setEmail(EMAIL_1);
        signupRequest.setPassword(PASSWORD_2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        signupRequest.setUsername(USERNAME_1);
        signupRequest.setEmail(EMAIL_2);
        signupRequest.setPassword(PASSWORD_2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
