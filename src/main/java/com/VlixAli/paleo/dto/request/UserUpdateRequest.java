package com.VlixAli.paleo.dto.request;

import com.VlixAli.paleo.annotation.NullOrNotBlank;

public record UserUpdateRequest(

        @NullOrNotBlank
        String username,
        @NullOrNotBlank
        String displayName,
        String bio
) {
}
