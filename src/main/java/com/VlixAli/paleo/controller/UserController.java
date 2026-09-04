package com.VlixAli.paleo.controller;

import com.VlixAli.paleo.dto.request.UserUpdateRequest;
import com.VlixAli.paleo.dto.response.UserResponse;
import com.VlixAli.paleo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.me(authentication);
    }

    @PatchMapping("/me")
    public UserResponse updateMe(Authentication authentication, @RequestBody @Valid UserUpdateRequest request) {
        return userService.updateMe(authentication, request);
    }


}
