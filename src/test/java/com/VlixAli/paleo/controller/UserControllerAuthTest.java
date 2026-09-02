package com.VlixAli.paleo.controller;

import com.VlixAli.paleo.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserShouldReturnProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .with(jwtWithRoles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-user"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void shouldReturnMultipleRolesStrippingPrefix() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .with(jwtWithRoles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.roles[1]").value("ADMIN"));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtWithRoles(
            String... roles) {
        var grantedAuthorities = java.util.Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toArray(SimpleGrantedAuthority[]::new);
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(grantedAuthorities)
                .jwt(builder -> builder
                        .subject("test-user")
                        .claim("preferred_username", "testuser")
                );
    }
}
