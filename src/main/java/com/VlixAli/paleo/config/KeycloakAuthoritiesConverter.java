package com.VlixAli.paleo.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeycloakAuthoritiesConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess =
                jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return List.of();
        }

        Object roles = realmAccess.get("roles");

        if (!(roles instanceof Collection<?> roleCollection)) {
            return List.of();
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Object role : roleCollection) {

            if (role instanceof String roleName) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                "ROLE_" + roleName
                        )
                );
            }
        }

        return authorities;
    }
}
