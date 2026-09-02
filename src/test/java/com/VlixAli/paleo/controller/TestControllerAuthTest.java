package com.VlixAli.paleo.controller;

import com.VlixAli.paleo.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import(SecurityConfig.class)
class TestControllerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserShouldAccessTestEndpoint() throws Exception {
        mockMvc.perform(get("/api/test")
                        .with(jwtWithRoles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Authenticated!"));
    }

    @Test
    void userRoleShouldAccessUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/user")
                        .with(jwtWithRoles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello USER"));
    }

    @Test
    void adminRoleOnlyShouldBeDeniedUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/user")
                        .with(jwtWithRoles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void userRoleShouldAccessUsersMeEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/users/me")
                        .with(jwtWithRoles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello USER"));
    }

    @Test
    void adminRoleShouldAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                        .with(jwtWithRoles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello ADMIN"));
    }

    @Test
    void userRoleShouldBeDeniedAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                        .with(jwtWithRoles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedRequestToAdminShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isUnauthorized());
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
