package agh.oot.librarby.user.dto;

import agh.oot.librarby.user.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Response payload containing user account information")
public record UserResponse(
        @Schema(description = "Unique identifier of the user", example = "1")
        Long id,

        @Schema(description = "Username of the user", example = "john_doe")
        String username,

        @Schema(description = "Email address of the user", example = "john_doe@example.com")
        String email,

        @Schema(description = "Role of the user in the system", example = "READER")
        UserRole role,

        @Schema(description = "First name of the user", example = "John")
        String firstName,

        @Schema(description = "Last name of the user", example = "Doe")
        String lastName,

        @Schema(description = "Additional data specific to the user's role",
                example = "{\"rentalLimit\": 5, \"dateOfBirth\": \"2001-09-11\"}")
        Map<String, Object> roleSpecificData
) {
}