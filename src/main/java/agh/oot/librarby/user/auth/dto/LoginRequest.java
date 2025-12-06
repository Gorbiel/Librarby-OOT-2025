package agh.oot.librarby.user.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
