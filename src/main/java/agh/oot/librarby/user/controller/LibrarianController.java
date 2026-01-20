package agh.oot.librarby.user.controller;

import agh.oot.librarby.user.dto.LibrarianCreateRequest;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.service.LibrarianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Librarians", description = "Endpoints for managing librarians")
@RestController
@RequestMapping(path = "/api/v1/librarians", produces = "application/json")
public class LibrarianController {
    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
    }

    @Operation(
            summary = "Create librarian",
            description = "Promotes an existing user to a librarian role. Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Librarian created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createLibrarian(@RequestBody @Valid LibrarianCreateRequest request) {
        UserResponse userResponse = librarianService.promoteUserToLibrarian(request.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
