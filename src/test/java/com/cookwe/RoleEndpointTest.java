package com.cookwe;

import com.cookwe.domain.service.RoleService;
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

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class RoleEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private static String cookie = "";

    @Test
    void testGetWitoutADMINROLE() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void getAllRoles() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getRolesByUserId() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("ROLE_USER"));
    }

    @Test
    void AddAndRemoveRole() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_ADMIN/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        roleService.addRoleToUser(TestUtils.USERNAME_1, "ROLE_ADMIN");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("ROLE_ADMIN"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_MODERATOR/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value("ROLE_MODERATOR"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/ROLE_MODERATOR/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("ROLE_ADMIN"));
    }

    @Test
    void testMultipleAdd() throws Exception {
        TestUtils.createuser(userService, TestUtils.USERNAME_1, TestUtils.EMAIL_1, TestUtils.PASSWORD_1);
        cookie = TestUtils.setUpSecurity(mockMvc, objectMapper, TestUtils.USERNAME_1, TestUtils.PASSWORD_1);

        roleService.addRoleToUser(TestUtils.USERNAME_1, "ROLE_ADMIN");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_ADMIN/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        roleService.addRoleToUser(TestUtils.USERNAME_1, "ROLE_MODERATOR");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_MODERATOR/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/ROLE_MODERATOR/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/ROLE_MODERATOR/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/RfgTORdfg/users/" + TestUtils.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(TestUtils.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
}
