package com.VlixAli.paleo.controller;

import com.VlixAli.paleo.dto.UserMeResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public UserMeResponse me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String id = jwt.getSubject();

        String username = jwt.getClaimAsString("preferred_username");

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority()
                        .replaceFirst("^ROLE_", ""))
                .toList();
        return new UserMeResponse(
                id,
                username,
                roles
        );
    }


}
