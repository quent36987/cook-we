package com.cookwe;

import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.entity.RecipePictureDTO;
import com.cookwe.domain.service.UserService;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class PictureEnpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static String cookie = "";

    @Test
    void testUploadPicture() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        RecipeDTO recipe = TestUtils.createRecipe(mockMvc, objectMapper, cookie, TestUtils.createSimpleRecipeRequest());

//        RecipePictureDTO reponse = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pictures/recipes/" + recipe.getId())
//                        .file(TestUtils.createMockMultipartFile())
//                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString(), RecipePictureDTO.class);

//        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/" + reponse.getImageUrl())
//                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().bytes("test".getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/sdfdsfdsfsdf.png")
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

//        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/recipes/" + recipe.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].imageUrl").value(reponse.getImageUrl()));
//
//        //delete picture
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/pictures/" + reponse.getImageUrl())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/pictures/" + reponse.getImageUrl())
//                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }


}
