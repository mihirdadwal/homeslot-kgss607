package com.kgs.homeslot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgs.homeslot.module.auth.dto.BuyerRegisterRequest;
import com.kgs.homeslot.module.auth.dto.LoginRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testBuyerRegistrationAndLoginFlow() throws Exception {
        BuyerRegisterRequest registerReq = new BuyerRegisterRequest();
        registerReq.setFullName("Mihir Test Buyer");
        registerReq.setEmail("buyer.test@homeslot.com");
        registerReq.setPhone("+12345678901");
        registerReq.setPassword("Password123!");
        registerReq.setCity("Mumbai");

        // 1. Register Buyer
        mockMvc.perform(post("/api/v1/auth/register/buyer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("buyer.test@homeslot.com"))
                .andExpect(jsonPath("$.data.role").value("ROLE_BUYER"));

        // 2. Login Buyer
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmailOrPhone("buyer.test@homeslot.com");
        loginReq.setPassword("Password123!");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    public void testRbacAdminEndpointAccessDeniedForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }
}
