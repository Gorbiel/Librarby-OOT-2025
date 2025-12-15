package agh.oot.librarby.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
