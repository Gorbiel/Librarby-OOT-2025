package agh.oot.librarby.user.controller;

import agh.oot.librarby.user.dto.LibrarianCreateRequest;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.service.LibrarianService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/librarians")
public class LibrarianController {
    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createLibrarian(@RequestBody @Valid LibrarianCreateRequest request) {
        UserResponse userResponse = librarianService.promoteUserToLibrarian(request.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
