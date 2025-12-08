package agh.oot.librarby.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserUpdateRequest(
        String username,

        @Email(message = "Invalid email format")
        String email,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,
        String firstName,
        String lastName
) {
}
