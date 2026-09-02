package com.VlixAli.paleo.dto;

import java.util.List;

public record UserMeResponse(
        String id,
        String username,
        List<String> roles
) {
}
