package agh.oot.librarby.user.dto;

import agh.oot.librarby.user.model.UserRole;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Integer rentalLimit

) {

}

