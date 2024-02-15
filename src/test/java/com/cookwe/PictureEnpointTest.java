package com.cookwe;

import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.response.RecipePictureResponse;
import com.cookwe.presentation.response.RecipeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class PictureEnpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static String cookie = "";

    @Test
    public void testUploadPicture() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        RecipeResponse recipe = UtilsTest.createRecipe(mockMvc, objectMapper, cookie, UtilsTest.createSimpleRecipeRequest());

        RecipePictureResponse reponse = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pictures/recipes/" + recipe.id)
                        .file(UtilsTest.createMockMultipartFile())
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), RecipePictureResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/" + reponse.imageUrl)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes("test".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/sdfdsfdsfsdf.png")
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/recipes/" + recipe.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].imageUrl").value(reponse.imageUrl));

        //delete picture
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/pictures/" + reponse.imageUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/" + reponse.imageUrl)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }


}
