package com.VlixAli.paleo.controller;

import com.VlixAli.paleo.dto.request.UserUpdateRequest;
import com.VlixAli.paleo.dto.response.UserResponse;
import com.VlixAli.paleo.security.SecurityConfig;
import com.VlixAli.paleo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void unauthenticatedGetShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedPatchShouldReturn401() throws Exception {
        mockMvc.perform(patch("/api/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bio\":\"hi\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedGetReturnsOwnProfile() throws Exception {
        when(userService.me(any())).thenReturn(response("alice"));

        mockMvc.perform(get("/api/users/me").with(jwt("user-a", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));

        ArgumentCaptor<Authentication> auth = ArgumentCaptor.forClass(Authentication.class);
        verify(userService).me(auth.capture());
        assertThat(((JwtAuthenticationToken) auth.getValue()).getToken().getSubject())
                .isEqualTo("user-a");
    }

    @Test
    void patchUpdatesOwnProfile() throws Exception {
        when(userService.updateMe(any(), any())).thenReturn(response("alice-new"));

        mockMvc.perform(patch("/api/users/me")
                        .with(jwt("user-a", "alice"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice-new\",\"bio\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice-new"));

        ArgumentCaptor<Authentication> auth = ArgumentCaptor.forClass(Authentication.class);
        ArgumentCaptor<UserUpdateRequest> request = ArgumentCaptor.forClass(UserUpdateRequest.class);
        verify(userService).updateMe(auth.capture(), request.capture());
        assertThat(((JwtAuthenticationToken) auth.getValue()).getToken().getSubject())
                .isEqualTo("user-a");
        assertThat(request.getValue().username()).isEqualTo("alice-new");
        assertThat(request.getValue().bio()).isEqualTo("hello");
    }

    @Test
    void patchBlankUsernameShouldReturn400() throws Exception {
        mockMvc.perform(patch("/api/users/me")
                        .with(jwt("user-a", "alice"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }

    private static UserResponse response(String username) {
        return new UserResponse(UUID.randomUUID(), username, username, null, Instant.now(), Instant.now());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt(
            String subject, String username) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder
                        .subject(subject)
                        .claim("preferred_username", username));
    }
}
