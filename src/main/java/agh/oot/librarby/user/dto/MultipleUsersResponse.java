package agh.oot.librarby.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response payload containing a list of users")
public record MultipleUsersResponse(List<UserResponse> users) {
}
