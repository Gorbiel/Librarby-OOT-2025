package agh.oot.librarby.auth.controller;

import agh.oot.librarby.auth.dto.LoginResponse;
import agh.oot.librarby.auth.dto.LoginRequest;
import agh.oot.librarby.auth.dto.RegisterReaderRequest;
import agh.oot.librarby.auth.service.AuthService;
import agh.oot.librarby.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Endpoints for managing user authentication")
@RestController
@RequestMapping(
        path = "/api/v1/auth",
        produces = MediaType.APPLICATION_JSON_VALUE
)
class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register new user", description = "Creates a new reader account in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reader registered successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
             @ApiResponse(
                     responseCode = "409",
                     description = "User already exists",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
             )
    })
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> registerNewReader(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New reader registration data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterReaderRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid RegisterReaderRequest request
    ) {
        authService.registerReader(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
