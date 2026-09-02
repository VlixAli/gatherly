package com.VlixAli.paleo.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakAuthoritiesConverterTest {

    private final KeycloakAuthoritiesConverter converter =
            new KeycloakAuthoritiesConverter();

    @Test
    void shouldExtractRolesFromRealmAccess() {
        Jwt jwt = jwt(Map.of(
                "realm_access", Map.of("roles", List.of("USER", "ADMIN"))
        ));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void shouldReturnEmptyWhenRealmAccessMissing() {
        Jwt jwt = jwt(Map.of());

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenRolesNotCollection() {
        Jwt jwt = jwt(Map.of(
                "realm_access", Map.of("roles", "USER")
        ));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenRolesListEmpty() {
        Jwt jwt = jwt(Map.of(
                "realm_access", Map.of("roles", List.of())
        ));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    private Jwt jwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("iss", "http://localhost:9999/realms/test")
                .claim("sub", "test-subject")
                .audience(List.of("test-audience"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claims(c -> c.putAll(claims))
                .build();
    }
}
