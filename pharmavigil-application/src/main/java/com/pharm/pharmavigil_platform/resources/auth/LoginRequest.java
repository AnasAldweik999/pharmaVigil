package com.pharm.pharmavigil_platform.resources.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "usernameOrEmail.required") String usernameOrEmail,
        @NotBlank(message = "password.required") String password,
        @NotBlank(message = "portalType.invalid") @Pattern(regexp = "STAFF|SUPERVISOR", message = "portalType.invalid") String portalType
) {
}
