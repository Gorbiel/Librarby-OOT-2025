package agh.oot.librarby.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the JWT token upon successful login")
public record LoginResponse(
        @Schema(
                description = "JSON Web Token to be used in the Authorization header",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String jwtToken
) {
}
