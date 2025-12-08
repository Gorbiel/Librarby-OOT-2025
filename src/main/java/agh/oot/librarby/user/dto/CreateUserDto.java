package agh.oot.librarby.user.dto;

import agh.oot.librarby.user.model.UserRole;

public record CreateUserDto(String username, String email, String password, UserRole role) {
}

