package com.VlixAli.paleo.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponse(
        String status,
        List<String> messages,
        int statusCode
) {
}
