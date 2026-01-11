package agh.oot.librarby.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@Schema(description = "Request payload for updating user account information")
public record UserUpdateRequest(
        @Schema(description = "New username for the user", example = "john_doe_123")
        String username,

        @Schema(description = "New email address for the user", example = "john_doe_123@example.com")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "New date of birth for the user", example = "1990-01-01")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Schema(description = "New first name of the user", example = "John")
        String firstName,

        @Schema(description = "New last name of the user", example = "Doe")
        String lastName
) {
}
