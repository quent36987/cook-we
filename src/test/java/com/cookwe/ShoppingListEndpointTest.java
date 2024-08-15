package com.cookwe;

import com.cookwe.domain.entity.RecipeDTO;
import com.cookwe.domain.entity.ShoppingListDTO;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.ShoppingListRecipeRequest;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ShoppingListEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static String cookie = "";

    @Test
    void testcreateList() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        String listname = "testlist";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/shopping-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listname)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(listname));

    }

    @Test
    void testaddrecipelist() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);
        RecipeDTO recipe = TestUtils.createRecipe(mockMvc, objectMapper, cookie, TestUtils.createSimpleRecipeRequest());

        String listname = "testlist";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/shopping-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listname)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(listname));

        List<ShoppingListDTO> shoppingListDTO =
                Arrays.asList(objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.get("/api/shopping-list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ShoppingListDTO[].class));


        ShoppingListRecipeRequest shoppingListRecipeRequest = new ShoppingListRecipeRequest();
        shoppingListRecipeRequest.setRecipeId(recipe.getId());
        shoppingListRecipeRequest.setPortion(2);
        shoppingListRecipeRequest.setIngredients(
                new ArrayList<>() {{
                    add("test");
                    add("test2");
                }}
        );


        mockMvc.perform(MockMvcRequestBuilders.post("/api/shopping-list/" + shoppingListDTO.get(0).getId() + "/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shoppingListRecipeRequest))
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());


        mockMvc.perform(MockMvcRequestBuilders.get("/api/shopping-list/" + shoppingListDTO.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

}
