package com.VlixAli.paleo.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String keycloakUserId,
        String username,
        String displayName,
        String homeCity,
        String workCity,
        String bio,
        Instant createdAt,
        Instant updatedAt
) {
}
