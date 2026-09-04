package com.VlixAli.paleo.dto.request;

import com.VlixAli.paleo.annotation.NullOrNotBlank;

public record UserUpdateRequest(

        @NullOrNotBlank
        String username,
        @NullOrNotBlank
        String displayName,
        @NullOrNotBlank
        String homeCity,
        @NullOrNotBlank
        String workCity,
        String bio
) {
}
