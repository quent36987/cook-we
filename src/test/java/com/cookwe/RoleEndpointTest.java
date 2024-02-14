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
public class RoleEndpointTest {
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
    public void testGetWitoutADMINROLE() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void getAllRoles() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getRolesByUserId() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].role").value("ROLE_USER"));
    }

    @Test
    public void AddAndRemoveRole() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_ADMIN/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        roleService.addRoleToUser(UtilsTest.USERNAME_1, "ROLE_ADMIN");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].role").value("ROLE_ADMIN"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_MODERATOR/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].role").value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].role").value("ROLE_MODERATOR"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/ROLE_MODERATOR/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].role").value("ROLE_ADMIN"));
    }

    @Test
    public void testMultipleAdd() throws Exception {
        UtilsTest.createuser(userService, UtilsTest.USERNAME_1, UtilsTest.EMAIL_1, UtilsTest.PASSWORD_1);
        cookie = UtilsTest.setUpSecurity(mockMvc, objectMapper, UtilsTest.USERNAME_1, UtilsTest.PASSWORD_1);

        roleService.addRoleToUser(UtilsTest.USERNAME_1, "ROLE_ADMIN");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_ADMIN/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        roleService.addRoleToUser(UtilsTest.USERNAME_1, "ROLE_MODERATOR");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roles/ROLE_MODERATOR/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/ROLE_MODERATOR/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/ROLE_MODERATOR/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/RfgTORdfg/users/" + UtilsTest.USERNAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(UtilsTest.COOKIE_NAME, cookie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
}
