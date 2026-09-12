package com.pharm.pharmavigil_platform.resources.auth;

import jakarta.validation.constraints.NotBlank;

public record SetupUsernameRequest(
        @NotBlank(message = "setupToken.required") String setupToken,
        @NotBlank(message = "username.required") String username
) {
}
