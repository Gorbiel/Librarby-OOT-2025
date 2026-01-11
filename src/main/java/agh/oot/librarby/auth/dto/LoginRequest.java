package agh.oot.librarby.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user login")
public record LoginRequest(
        @Schema(description = "Username of the user trying to log in", example = "john_doe")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Password of the user trying to log in", example = "P@ssw0rd!")
        @NotBlank(message = "Password is required")
        String password
) {
}
