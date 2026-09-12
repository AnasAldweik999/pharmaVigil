package com.pharm.pharmavigil_platform.resources.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "token.required") String token,
        @NotBlank(message = "newPassword.invalid.format") @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#^()\\-+=])[A-Za-z\\d@$!%*?&_#^()\\-+=]{8,}$",
                message = "newPassword.invalid.format"
        ) String newPassword
) {
}
