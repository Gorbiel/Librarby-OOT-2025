package agh.oot.librarby.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Request payload for registering a new reader")
public record RegisterReaderRequest(
        @Schema(description = "Desired username for the new reader account", example = "john_doe")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(description = "Password for the new reader account", example = "P@ssw0rd!")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @Schema(description = "Email address of the new reader", example = "john_doe@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "First name of the new reader", example = "John")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Last name of the new reader", example = "Doe")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Date of birth of the new reader", example = "2001-09-11")
        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth
) {
}
