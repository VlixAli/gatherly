package com.VlixAli.paleo.service;

import com.VlixAli.paleo.dto.request.UserUpdateRequest;
import com.VlixAli.paleo.entity.User;
import com.VlixAli.paleo.mapper.UserMapperImpl;
import com.VlixAli.paleo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@Import({UserService.class, UserMapperImpl.class})
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void userACannotUpdateUserB() {
        User userB = userRepository.save(User.builder()
                .keycloakId("kc-bob").username("bob").displayName("Bob").bio("bob bio").build());
        userRepository.save(User.builder()
                .keycloakId("kc-alice").username("alice").displayName("Alice").build());

        var response = userService.updateMe(
                auth("kc-alice", "alice", "Alice"),
                new UserUpdateRequest("alice-new", null, "alice bio"));

        assertThat(response.username()).isEqualTo("alice-new");
        User reloadedB = userRepository.findById(userB.getId()).orElseThrow();
        assertThat(reloadedB.getUsername()).isEqualTo("bob");
        assertThat(reloadedB.getDisplayName()).isEqualTo("Bob");
        assertThat(reloadedB.getBio()).isEqualTo("bob bio");
    }

    @Test
    void unknownKeycloakIdCreatesUser() {
        var response = userService.me(auth("kc-new", "newbie", null));

        assertThat(response.username()).isEqualTo("newbie");
        assertThat(response.displayName()).isEqualTo("newbie");
        assertThat(userRepository.findByKeycloakId("kc-new")).isPresent();
    }

    @Test
    void existingUserReturnsExistingWithoutDuplicate() {
        userRepository.save(User.builder()
                .keycloakId("kc-alice").username("alice").displayName("Alice").build());

        var response = userService.me(auth("kc-alice", "alice-changed", "Changed"));

        assertThat(response.username()).isEqualTo("alice");
        assertThat(userRepository.count()).isEqualTo(1);
    }

    private static JwtAuthenticationToken auth(String subject, String username, String name) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .claim("preferred_username", username);
        if (name != null) {
            builder.claim("name", name);
        }
        return new JwtAuthenticationToken(builder.build());
    }
}
