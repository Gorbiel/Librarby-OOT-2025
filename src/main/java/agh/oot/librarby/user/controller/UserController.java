package agh.oot.librarby.user.controller;

import agh.oot.librarby.exception.ApiErrorResponse;
import agh.oot.librarby.user.dto.MultipleUsersResponse;
import agh.oot.librarby.user.dto.UserResponse;
import agh.oot.librarby.user.dto.UserUpdateRequest;
import agh.oot.librarby.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Endpoints for managing user accounts")
@RestController
@RequestMapping(
        path = "/api/v1/users",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all user accounts in the system. Requires admin privileges.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MultipleUsersResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied – admin privileges required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<MultipleUsersResponse> getAllUsers() {
        MultipleUsersResponse body = userService.getAllUserAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user account by its unique ID. Requires admin or librarian privileges.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User account retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied – admin or librarian privileges required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN') or #userId == principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserAccount(userId));
    }

    @Operation(summary = "Update user by ID", description = "Updates the details of a user account identified by its unique ID. Requires admin or librarian privileges.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User account updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied – admin or librarian privileges required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PatchMapping(
            value = "/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN') or #userId == principal.id")
    public ResponseEntity<UserResponse> updateUserById(
            @Parameter(description = "User account ID", example = "123", required = true)
            @PathVariable("userId") Long userAccountId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fields to update for the user account",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody
            @Valid UserUpdateRequest request
    ) {
        UserResponse userResponse = userService.updateUserAccount(userId, request);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user account identified by its unique ID. Requires admin privileges.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User account deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – authentication required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied – admin privileges required",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> deleteUserById(
            @Parameter(description = "User account ID", example = "123", required = true)
            @PathVariable("userId") Long userAccountId
    ) {
        userService.deleteUserAccount(userAccountId);
        return ResponseEntity.noContent().build();
}
}
