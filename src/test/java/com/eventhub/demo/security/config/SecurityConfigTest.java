package com.eventhub.demo.security.config;

import com.eventhub.demo.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Test
    public void pubicEndpoints_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    public void privateEndpoints_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void protectedEndpoints_withAuthenticatedUser_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk());
    }

    @Test
    public void protectedEndpoints_withValidJwtToken_shouldBeAccessible() throws Exception {
        // Given
        String token = "valid.jwt.token";
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(jwtService.isTokenValid(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/events")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void protectedEndpoints_withInvalidJwtToken_shouldNotBeAccessible() throws Exception {
        // Given
        String token = "invalid.jwt.token";
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}