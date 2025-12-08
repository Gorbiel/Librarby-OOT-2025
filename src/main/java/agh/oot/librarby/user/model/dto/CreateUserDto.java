package agh.oot.librarby.user.model.dto;

import agh.oot.librarby.user.model.UserRole;

public record CreateUserDto(String username, String email, String password, UserRole role){}

