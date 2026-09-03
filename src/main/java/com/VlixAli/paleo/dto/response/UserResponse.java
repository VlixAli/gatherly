package com.VlixAli.paleo.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String displayName,
        String bio,
        Instant createdAt,
        Instant updatedAt
) {
}
