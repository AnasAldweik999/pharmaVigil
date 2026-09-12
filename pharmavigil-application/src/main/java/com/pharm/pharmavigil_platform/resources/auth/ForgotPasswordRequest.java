package com.pharm.pharmavigil_platform.resources.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "email.required") @Email(message = "email.format.invalid") String email
) {
}
